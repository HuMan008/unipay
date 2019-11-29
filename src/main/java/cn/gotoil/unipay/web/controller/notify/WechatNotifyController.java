package cn.gotoil.unipay.web.controller.notify;

import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.NotifyAccept;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.utils.UtilMySign;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.utils.UtilWechat;
import cn.gotoil.unipay.web.helper.RedisLockHelper;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 微信通知入口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-27、11:48
 */
@Slf4j
@RequestMapping("payment/wechat")
@Controller
public class WechatNotifyController {

    @Autowired
    RedisLockHelper redisLockHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    ChargeConfigService chargeConfigService;
    @Autowired
    AppService appService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    NotifyAcceptService notifyAcceptService;

    @RequestMapping(value = {"{orderId:^\\d{21}$}"})
    @NeedLogin(value = false)
    public void asyncNotify(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                              @PathVariable String orderId) throws Exception {

        httpServletResponse.setCharacterEncoding(Charsets.ISO_8859_1.name());
        String requestBodyXml = "";//响应xml
        Map<String, String> mm = new HashMap<>();
        NotifyAccept notifyAccept = NotifyAcceptService.createDefault(httpServletRequest, EnumOrderMessageType.PAY,
                orderId);
        notifyAccept.setOrderId(orderId);

        try {
            //判断是否正在处理这个订单
            if (redisLockHelper.hasLock(RedisLockHelper.Key.Notify + orderId)) {
                mm.put(WechatService.RETURN_CODE, "FAIL");
                mm.put("return_msg", "OrderProcessing");
                notifyAccept.setResponstr("FAIL:订单处理中");
                httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                return;
            }
            redisLockHelper.addLock(RedisLockHelper.Key.Notify + orderId, false, 0, null);
            requestBodyXml = CharStreams.toString(httpServletRequest.getReader());
            notifyAccept.setParams(requestBodyXml);
            //响应XML转成Map
            Map<String, String> reMap = UtilWechat.xmlToMap(requestBodyXml);
            log.info("微信异步通知【{}】\n{}",orderId,JSONObject.toJSONString(reMap));
            if (reMap.containsKey(WechatService.RETURN_CODE) && WechatService.SUCCESS.equals(reMap.get(WechatService.RETURN_CODE).toString())) {
                //路径里的订单号不等于通知内容里的商户订单号，直接返回处理失败
                if (!orderId.equals(reMap.get("out_trade_no"))) {
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "OrderNOError");
                    notifyAccept.setResponstr("FAIL:订单号错误");
                    httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                    return ;
                }
                //订单查询
                Order order = orderService.loadByOrderID(orderId);
                if (order == null) {
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "OrderNotExists");
                    notifyAccept.setResponstr("FAIL:订单不存在");
                    httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                    return ;
                }
                notifyAccept.setAppOrderNo(order.getAppOrderNo());
                //订单不是支付中状态
                if (EnumOrderStatus.PaySuccess.getCode() == order.getStatus().byteValue()) {
                    mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                    mm.put("return_msg", "Doed,skip");
                    notifyAccept.setResponstr("SKIP:已处理的订单");
                    httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                    return ;
                }
                if (EnumOrderStatus.Created.getCode() != order.getStatus().byteValue()) {
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "LocationStatusError");
                    notifyAccept.setResponstr("FAIL:本地订单状态不正确");
                    httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                    return ;
                }
                //支付方式 APP NATIVATE JSAPI
                String trade_type = reMap.get("trade_type");
                //根据订单找到这个订单的收款账号
                ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
                ChargeWechatModel model = JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                        ChargeWechatModel.class);

                String merchanKey = model == null ? "" : model.getApiKey();
                if (UtilWechat.isSignatureValid(reMap, merchanKey) && model.getAppID().equalsIgnoreCase(reMap.get(
                        "appid"))) {
                    //支付成功
                    if (WechatService.SUCCESS.equalsIgnoreCase(reMap.get(WechatService.RESULT_CODE))) {
                        //                        更新订单
                        Order newOrder = new Order();
                        newOrder.setId(order.getId());
                        newOrder.setPayFee(Integer.parseInt(reMap.get("cash_fee")));
                        newOrder.setArrFee(Integer.parseInt(reMap.get("total_fee")));
                        newOrder.setStatus(EnumOrderStatus.PaySuccess.getCode());
                        if (StringUtils.isNotEmpty(reMap.get("time_end"))) {
                            //yyyyMMddHHmmss
                            newOrder.setOrderPayDatetime(DateUtils.simpleDateTimeNoSymbolFormatter().parse(reMap.get(
                                    "time_end")).getTime() / 1000);
                        } else {
                            newOrder.setOrderPayDatetime(0L);
                        }
                        newOrder.setPaymentUid(reMap.get("openid"));
                        newOrder.setPaymentId(reMap.get("transaction_id"));
                        notifyAccept.setPaymentId(newOrder.getPaymentId());
                        int x = orderService.updateOrder(order, newOrder);
                        if (x != 1) {
                            mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                            mm.put("return_msg", "LocationUpdateFail");
                            notifyAccept.setResponstr("FAIL:本地订单状态更新失败");
                            httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                            return;
                        } else {
                            OrderNotifyBean orderNotifyBean =
                                    OrderNotifyBean.builder()
                                            .unionOrderID(order.getId())
                                            .appId(order.getAppId())
                                            .method(EnumOrderMessageType.PAY.name())
                                            .appOrderNO(order.getAppOrderNo())
                                            .status(newOrder.getStatus())
                                            .orderFee(order.getFee())
                                            .paymentId(newOrder.getPaymentId())
                                            .payFee(newOrder.getPayFee())
                                            .refundFee(0)
                                            .totalRefundFee(0)
                                            .asyncUrl(order.getAsyncUrl())
                                            .extraParam(order.getExtraParam())
                                            .payDate(newOrder.getOrderPayDatetime())
                                            .arrFee(newOrder.getArrFee())
                                            .timeStamp(Instant.now().getEpochSecond()).build();
                            String appSecret = appService.key(order.getAppId());
                            String signStr = UtilMySign.sign(orderNotifyBean, appSecret);
                            orderNotifyBean.setSign(signStr);
                            rabbitTemplate.convertAndSend(ConstsRabbitMQ.ORDERFIRSTEXCHANGENAME,
                                    ConstsRabbitMQ.ORDERROUTINGKEY, JSON.toJSONString(orderNotifyBean));

                            mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                            mm.put("return_msg", "OK");
                            notifyAccept.setResponstr("OK:并发送通知");
                            httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                            return ;
                        }
                    } else {
                        //  不是成功状态
                        mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                        mm.put("return_msg", "NotifyStatusNotSuccess");
                        notifyAccept.setResponstr("OK:不是成功状态，不处理");
                        httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                        return;
                    }
                } else {
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "VerifyError");
                    notifyAccept.setResponstr("FAIL:签名验证失败");
                    httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                    return ;
                }
            } else {
                mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                mm.put("return_msg", "MessageError");
                notifyAccept.setResponstr("FAIL:消息为不正常消息");
                httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                return ;
            }
        } catch (Exception e) {
            log.error("微信支付异步通知处理异常{}", e);
            mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
            mm.put("return_msg", e.getMessage());
            notifyAccept.setResponstr(UtilString.getLongString("FAIL:" + e.getMessage(), 4000));
            httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
            return ;

        } finally {
            redisLockHelper.releaseLock(RedisLockHelper.Key.Notify + orderId);
            notifyAcceptService.add(notifyAccept);
        }
    }

    @NeedLogin(value = false)
    @RequestMapping("return/{orderId:^\\d{21}$}")
    public ModelAndView syncNotify(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                   @PathVariable String orderId) throws Exception {
        log.info("微信同步通知");
        //稍微等一下异步通知
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return orderService.syncUrl(orderId, httpServletRequest, httpServletResponse);
    }
}
