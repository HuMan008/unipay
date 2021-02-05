package cn.gotoil.unipay.web.controller.notify;

import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.futrue.RefundFutrue;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.NotifyAccept;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.utils.UtilMoney;
import cn.gotoil.unipay.utils.UtilMySign;
import cn.gotoil.unipay.utils.UtilRequest;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.web.helper.AlipayConfigHelper;
import cn.gotoil.unipay.web.helper.RedisLockHelper;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
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

    @Autowired
    NotifyAcceptService notifyAcceptService;
    @Autowired
    RefundService refundService;


    @RequestMapping(value = {"{orderId:^\\d{21}$}"}, method = RequestMethod.POST)
    @NeedLogin(value = false)
    public void asyncNotify(Model model, HttpServletRequest request, HttpServletResponse httpServletResponse, @PathVariable String orderId) {
        Map<String, String> params = UtilRequest.request2Map(request);
        NotifyAccept notifyAccept = NotifyAcceptService.createDefault(request, EnumOrderMessageType.PAY, orderId);
        log.debug("支付宝异步通知【{}】\n", orderId, JSONObject.toJSONString(params));
        try {
            //判断是否正在处理这个订单
            if (redisLockHelper.hasLock(RedisLockHelper.Key.Notify + orderId)) {
                log.error("支付宝订单【{}】异步通知处理冲突，忽略本次通知", orderId);
                notifyAccept.setResponstr("error:处理中");
                httpServletResponse.getOutputStream().print("error");
                return;
            }
            redisLockHelper.addLock(RedisLockHelper.Key.Notify + orderId, false, 0, null);
            //订单查询
            Order order = orderService.loadByOrderID(orderId);
            if (order == null) {
                log.error("支付宝订单【{}】异步通知处理失败，本地订单未查询到", orderId);
                notifyAccept.setResponstr("error:本地未找到订单");
                httpServletResponse.getOutputStream().print("error");
                return;
            }
            notifyAccept.setAppOrderNo(order.getAppOrderNo());

            ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
            ChargeAlipayModel chargeAlipayModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeAlipayModel.class);
            //开始验签
            boolean signVerified =
                    AlipayConfigHelper.getInstance().getFactory(chargeAlipayModel.getAppID()).Common().verifyNotify(params);
            //            boolean signVerified = AlipaySignature.rsaCheckV1(params, chargeAlipayModel.getPubKey(),
            //                    Charsets.UTF_8.name(), AlipayService.SIGNTYPE);
            //调用SDK验证签名 并且判断通知里的APPID是不是等于订单收款账户的APPID
            if (signVerified && chargeAlipayModel.getAppID().equalsIgnoreCase(params.get("app_id")) && orderId.equalsIgnoreCase(params.get("out_trade_no"))) {
                if(isPayNotify(params)){
                    //订单不是支付中状态
                    if (EnumOrderStatus.Created.getCode() != order.getStatus().byteValue()) {
                        log.error("支付宝订单【{}】异步通知处理失败，订单状态不是待支付", orderId);
                        notifyAccept.setResponstr("error:本地未找到订单");
                        httpServletResponse.getOutputStream().print("error");
                        return;
                    }
                    //支付通知
                    processPayNotify(params,order,notifyAccept,httpServletResponse,orderId);
                }else{
                    //退款通知
                    processRefundNotify(params,notifyAccept,httpServletResponse);
                }
            } else {
                log.error("支付宝订单【{}】异步通知签名验验证失败", orderId);
                notifyAccept.setResponstr("error:签名验验证失败");
                httpServletResponse.getOutputStream().print("error");
                return;
            }

        } catch (Exception e) {
            log.error("支付宝订单【{}】异步通知处理失败{}", orderId,e);
            notifyAccept.setResponstr(UtilString.getLongString("error:异常" + e.getMessage(), 4000));
            try {
                httpServletResponse.getOutputStream().print("error");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            redisLockHelper.releaseLock(RedisLockHelper.Key.Notify + orderId);
            notifyAcceptService.add(notifyAccept);
        }

    }

    @NeedLogin(value = false)
    @RequestMapping(value = "/return/{orderId:^\\d{21}$}", method = RequestMethod.GET)
    public ModelAndView syncNotify(@PathVariable String orderId, HttpServletRequest httpServletRequest,
                                   HttpServletResponse httpServletResponse) throws Exception {
        log.debug("支付宝同步通知{}",orderId);
        //稍微等一下异步通知
//        try {
//            Thread.sleep(RandomUtils.nextInt(500, 1200));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        return orderService.syncUrl(orderId, httpServletRequest, httpServletResponse);

    }


    /**
     * 判断是支付通知
     * @param paramMap
     * @return true是支付通知  false不是支付通知
     */
    private boolean isPayNotify(Map<String,String> paramMap ){
        if(paramMap.containsKey("out_biz_no") && paramMap.containsKey("refund_fee") && paramMap.containsKey(
                "gmt_refund")){
            return false;
        }
        return true;
    }

    private void processPayNotify(Map<String,String> params,Order order,NotifyAccept notifyAccept,
                                  HttpServletResponse httpServletResponse,String orderId) throws Exception{
        //支付异步通知
        if ("trade_status_sync".equalsIgnoreCase(params.get("notify_type"))) {
            ///new BigDecimal(params.get("buyer_pay_amount")).multiply(new BigDecimal(100)).intValue()
            if ("TRADE_SUCCESS".equals(params.get("trade_status")) && UtilMoney.yuanToFen(params.get(
                    "total_amount")) == order.getFee()) {
                Order newOrder = new Order();
                newOrder.setId(order.getId());
                newOrder.setPayFee(UtilMoney.yuanToFen(params.get("buyer_pay_amount")));
                newOrder.setArrFee(UtilMoney.yuanToFen(params.get("receipt_amount")));
                newOrder.setStatus(EnumOrderStatus.PaySuccess.getCode());
                if (StringUtils.isNotEmpty(params.get("gmt_payment"))) {
                    //yyyy-MM-dd HH:mm:ss
                    newOrder.setOrderPayDatetime(DateUtils.simpleDatetimeFormatter().parse(params.get(
                            "gmt_payment")).getTime() / 1000);
                } else {
                    newOrder.setOrderPayDatetime(0L);
                }
                newOrder.setPaymentUid(UtilString.getLongString(params.get("buyer_logon_id"), 50));
                newOrder.setPaymentId(params.get("trade_no"));
                notifyAccept.setPaymentId(newOrder.getPaymentId());
                int x = orderService.updateOrder(order, newOrder);
                if (x != 1) {
                    log.error("支付宝订单【{}】更新失败{}", orderId, newOrder);
                    notifyAccept.setResponstr("error:更新订单状态失败");
                    httpServletResponse.getOutputStream().print("error");
                }
                log.info("支付宝订单【{}】异步通知处理，订单状态更新{}", orderId, x == 1 ? "成功" : "失败");
                OrderNotifyBean orderNotifyBean =
                        OrderNotifyBean.builder().unionOrderID(order.getId()).method(EnumOrderMessageType.PAY.name()).appId(order.getAppId()).paymentId(newOrder.getPaymentId()).appOrderNO(order.getAppOrderNo()).status(newOrder.getStatus()).orderFee(order.getFee()).payFee(newOrder.getPayFee()).arrFee(newOrder.getArrFee()).asyncUrl(order.getAsyncUrl()).extraParam(order.getExtraParam()).payDate(newOrder.getOrderPayDatetime()).
                                timeStamp(Instant.now().getEpochSecond()).build();
                String appSecret = appService.key(order.getAppId());
                String signStr = UtilMySign.sign(orderNotifyBean, appSecret);
                orderNotifyBean.setSign(signStr);
                rabbitTemplate.convertAndSend(ConstsRabbitMQ.ORDERFIRSTEXCHANGENAME,
                        ConstsRabbitMQ.ORDERROUTINGKEY, JSON.toJSONString(orderNotifyBean));
                notifyAccept.setResponstr("success:发通知");
                httpServletResponse.getOutputStream().print("success");
                log.info("支付宝订单【{}】异步通知完成并返回SUCCESS,消息加入队列", orderId);
                return;
            } else if ("TRADE_CLOSED".equals(params.get("trade_status"))) {
                // 未付款交易超时关闭，或支付完成后全额退款
                //订单关闭通知
                log.info("支付宝订单【{}】异步通知 订单关闭{}", orderId, params);
                // 订单关闭通知不处理订单状态
                notifyAccept.setResponstr("success:订单已关闭");
                httpServletResponse.getOutputStream().print("success");
                return;
            } else {
                // TRADE_FINISHED                   交易结束，不可退款
                log.error("支付宝订单【{}】异步通知状态不是支付成功{}", orderId, params);
                notifyAccept.setResponstr("success:订单状态不是成功状态");
                httpServletResponse.getOutputStream().print("error");
                return;
            }
        } else {
            log.error("支付宝订单【{}】异步通知类型未确定", orderId);
            notifyAccept.setResponstr("error:method未知");
            httpServletResponse.getOutputStream().print("error");
            return;
        }
    }



    private void processRefundNotify(Map<String,String> params,NotifyAccept notifyAccept,
                                     HttpServletResponse httpServletResponse) throws Exception{
        String refundOrderId = params.get("out_biz_no");
        Refund refund  = refundService.loadById(refundOrderId);
        if(refund==null){
            log.error("支付宝订单【{}】退款通知处理失败{}", params);
            notifyAccept.setResponstr("out_biz_no_error"+ refundOrderId);
            httpServletResponse.getOutputStream().print("error");
            return;
        }

        Refund newRefund  = new Refund();
        newRefund.setRefundOrderId(refundOrderId);
        newRefund.setStatusUpdateDatetime(new Date());
        newRefund.setUpdateAt(new Date());
        byte ps = refund.getProcessResult();

        //部分退款 ，全额退款 都是退款成功
        if("REFUND_SUCCESS".equalsIgnoreCase(params.get("refund_status")) || "TRADE_CLOSED".equalsIgnoreCase(params.get("trade_status"))){
            ps= EnumRefundStatus.Success.getCode();
            if (StringUtils.isNotEmpty(params.get("gmt_refund"))) {
                //2017-12-15 09:46:01
                newRefund.setStatusUpdateDatetime(DateUtils.simpleDatetimeFormatter().parse(params.get("gmt_refund")));
            }
            newRefund.setFee(UtilMoney.yuanToFen(params.get("refund_fee")));
        }else {
            ps= EnumRefundStatus.Failed.getCode();
        }
        newRefund.setProcessResult(ps);
        //更新refund
        int x = refundService.updateRefund(refund, newRefund);
        //  发送通知
        if (x == 1 && newRefund.getProcessResult().byteValue()!=refund.getProcessResult().byteValue()) {
            RefundFutrue refundFutrue = new RefundFutrue(refund.getRefundOrderId(),refundService,orderService
                    ,appService,rabbitTemplate);
            refundFutrue.afterFetchRefundResult(false);
            notifyAccept.setResponstr("退款状态更新并发送通知");
            httpServletResponse.getOutputStream().print("success");
            return;
        }else{
            notifyAccept.setResponstr("退款状态更新出错");
            httpServletResponse.getOutputStream().print("error");
            log.error("【{}】退款状态更新出错", refund.getRefundOrderId());
            return;
        }
    }

}
