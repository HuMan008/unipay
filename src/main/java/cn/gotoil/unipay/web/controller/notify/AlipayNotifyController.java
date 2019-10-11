package cn.gotoil.unipay.web.controller.notify;

import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.utils.UtilMoney;
import cn.gotoil.unipay.utils.UtilMySign;
import cn.gotoil.unipay.utils.UtilRequest;
import cn.gotoil.unipay.web.helper.RedisLockHelper;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import cn.gotoil.unipay.web.services.OrderService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Map;

/**
 * 支付宝通知
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-27、11:48
 */
@Slf4j
@RequestMapping("payment/alipay")
@Controller
public class AlipayNotifyController {

    @Autowired
    RedisLockHelper redisLockHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    ChargeConfigService chargeConfigService;

    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    AppService appService;

    @RequestMapping(value = {"{orderId:^\\d{21}$}"})
    @NeedLogin(value = false)
    public String asyncNotify(Model model, HttpServletRequest request, HttpServletResponse httpServletResponse,
                              @PathVariable String orderId) throws UnsupportedEncodingException, AlipayApiException {
        Map<String, String> params = UtilRequest.request2Map(request);
        log.debug("支付宝异步通知{}", params);
        try {
            //判断是否正在处理这个订单
            if (redisLockHelper.hasLock(RedisLockHelper.Key.Notify + orderId)) {
                log.error("支付宝订单【{}】异步通知处理冲突，忽略本次通知", orderId);
                return "error";
            }
            redisLockHelper.addLock(RedisLockHelper.Key.Notify + orderId, false, 0, null);
            //订单查询
            Order order = orderService.loadByOrderID(orderId);
            if (order == null) {
                log.error("支付宝订单【{}】异步通知处理失败，本地订单为查询到", orderId);
                return "error";
            }
            //订单不是支付中状态
            if (EnumOrderStatus.Created.getCode() != order.getStatus().byteValue()) {
                log.error("支付宝订单【{}】异步通知处理失败，订单状态不是待支付", orderId);
                return "error";
            }
            ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
            ChargeAlipayModel chargeAlipayModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeAlipayModel.class);
            //开始验签
            boolean signVerified = AlipaySignature.rsaCheckV1(params, chargeAlipayModel.getPubKey(),
                    Charsets.UTF_8.name());
            //调用SDK验证签名 并且判断通知里的APPID是不是等于订单收款账户的APPID
            if (signVerified && chargeAlipayModel.getAppID().equalsIgnoreCase(params.get("app_id")) && orderId.equalsIgnoreCase(params.get("out_trade_no"))) {
                //支付异步通知
                if ("trade_status_sync".equalsIgnoreCase(params.get("notify_type"))) {
                    ///new BigDecimal(params.get("buyer_pay_amount")).multiply(new BigDecimal(100)).intValue()
                    if ("TRADE_SUCCESS".equals(params.get("trade_status")) && UtilMoney.yuanToFen(params.get(
                            "total_amount")) == order.getFee()) {
                        Order newOrder = new Order();
                        newOrder.setId(order.getId());
                        newOrder.setPayFee(UtilMoney.yuanToFen(params.get("buyer_pay_amount")));
                        newOrder.setStatus(EnumOrderStatus.PaySuccess.getCode());
                        if (StringUtils.isNotEmpty(params.get("gmt_payment"))) {
                            //yyyy-MM-dd HH:mm:ss
                            newOrder.setOrderPayDatetime(DateUtils.simpleDatetimeFormatter().parse(params.get(
                                    "gmt_payment")).getTime());
                        } else {
                            newOrder.setOrderPayDatetime(0L);
                        }
                        newOrder.setPaymentUid(params.get("buyer_id"));
                        newOrder.setPaymentId(params.get("trade_no"));
                        int x = orderService.updateOrder(order, newOrder);
                        if (x != 1) {
                            log.error("支付宝订单【{}】更新失败{}", orderId, newOrder);
                            return "error";
                        }
                        OrderNotifyBean orderNotifyBean =
                                OrderNotifyBean.builder().unionOrderID(order.getId())
                                        .method(EnumOrderMessageType.PAY.name())
                                        .appOrderNO(newOrder.getPaymentId())
                                        .status(newOrder.getStatus())
                                        .orderFee(order.getFee())
                                        .refundFee(0)
                                        .totalRefundFee(0)
                                        .asyncUrl(order.getAsyncUrl())
                                        .extraParam(order.getExtraParam())
                                        .payDate(newOrder.getOrderPayDatetime()).
                                        timeStamp(Instant.now().getEpochSecond())
                                        .build();
                        String appSecret = appService.key(order.getAppId());
                        String signStr = UtilMySign.sign(orderNotifyBean, appSecret);
                        orderNotifyBean.setSign(signStr);
                        rabbitTemplate.convertAndSend(ConstsRabbitMQ.orderFirstExchangeName,
                                ConstsRabbitMQ.orderRoutingKey, JSON.toJSONString(orderNotifyBean));
                        return "success";
                    } else if ("TRADE_CLOSED".equals(params.get("trade_status"))) {
                        Order newOrder = new Order();
                        newOrder.setId(order.getId());
                        newOrder.setPayFee(0);
                        newOrder.setStatus(EnumOrderStatus.PaySuccess.getCode());
                        orderService.updateOrder(order, newOrder);
                        log.info("支付宝订单【{}】异步通知 订单关闭{}", orderId, params);
                        return "success";
                    } else {
                        log.error("支付宝订单【{}】异步通知状态不是支付成功{}", orderId, params);
                        return "error";
                    }
                } else {
                    log.error("支付宝订单【{}】异步通知类型未确定", orderId);
                    return "error";
                }
            } else {
                log.error("支付宝订单【{}】异步通知签名验验证失败", orderId);
                return "error";
            }

        } catch (Exception e) {
            log.error("支付宝订单【{}】异步通知处理失败", orderId);
            return "error";
        } finally {
            redisLockHelper.releaseLock(RedisLockHelper.Key.Notify + orderId);
        }

    }

    @NeedLogin(value = false)
    @RequestMapping("return/{orderId:^\\d{21}$}}")
    public void syncNotify(@PathVariable String orderId) {
        log.info("支付宝同步通知");

    }
}
