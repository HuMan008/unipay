package cn.gotoil.unipay.web.controller.notify;

import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.futrue.RefundFutrue;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.NotifyAccept;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.utils.*;
import cn.gotoil.unipay.web.helper.RedisLockHelper;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jcajce.provider.digest.MD5;
import org.bouncycastle.math.raw.Mod;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
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

    @Autowired
    RefundService refundService;
    @Value("${domain}")
    String domain;

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
                            log.info("微信订单【{}】异步通知处理，订单状态更新失败", orderId);
                            return;
                        } else {
                            log.info("微信订单【{}】异步通知处理，订单状态更新成功", orderId);
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
                            log.info("微信支付订单【{}】异步通知完成并返回SUCCESS,消息加入队列", orderId);
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


    /**
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param orderId 统一支付订单号
     * @param refundOrderId 统一退款订单号
     * @throws Exception
     */
    @RequestMapping(value = {"refund/{orderId:^\\d{21}$}/{refundOrderId}"})
    @NeedLogin(value = false)
    public void asyncRefundNotify(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                            @PathVariable String orderId,
                            @PathVariable String refundOrderId) throws Exception {

        httpServletResponse.setCharacterEncoding(Charsets.ISO_8859_1.name());
        String requestBodyXml = "";//响应xml
        Map<String, String> mm = new HashMap<>();
        NotifyAccept notifyAccept = NotifyAcceptService.createDefault(httpServletRequest, EnumOrderMessageType.REFUND,
                orderId);
        try {
            //判断是否正在处理这个订单
            if (redisLockHelper.hasLock(RedisLockHelper.Key.RefundNotify + refundOrderId)) {
                mm.put(WechatService.RETURN_CODE, "FAIL");
                mm.put("return_msg", "OrderProcessing");
                notifyAccept.setResponstr("FAIL:退款通知处理中");
                httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                return;
            }
            redisLockHelper.addLock(RedisLockHelper.Key.RefundNotify + refundOrderId, false, 0, null);
            requestBodyXml = CharStreams.toString(httpServletRequest.getReader());
            notifyAccept.setParams(requestBodyXml);
            //响应XML转成Map
            Map<String, String> reMap = UtilWechat.xmlToMap(requestBodyXml);

            log.info("微信退款异步通知【{}】\n{}",refundOrderId,JSONObject.toJSONString(reMap));
            //响应code 为success
            if (reMap.containsKey(WechatService.RETURN_CODE) && WechatService.SUCCESS.equals(reMap.get(WechatService.RETURN_CODE).toString())) {

                //查询退款信息
                Refund refund = refundService.loadById(refundOrderId);
                if(refund==null){
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "RefundNotExists");
                    notifyAccept.setResponstr("FAIL:退款信息不存在");
                    httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                    return ;
                }
                //订单查询
                Order order = orderService.loadByOrderID(refund.getOrderId());
                if (order == null) {
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "OrderNotExists");
                    notifyAccept.setResponstr("FAIL:支付订单不存在");
                    httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                    return ;
                }
                notifyAccept.setAppOrderNo(order.getAppOrderNo());

                //退款信息不是处理中
                if (EnumRefundStatus.WaitSure.getCode() != refund.getProcessResult().byteValue()) {
                    mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                    mm.put("return_msg", "success");
                    notifyAccept.setResponstr("SKIP:已处理的订单");
                    httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                    return ;
                }
                //根据订单找到这个订单的收款账号
                ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
                ChargeWechatModel model = JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                        ChargeWechatModel.class);
                String merchanKey = model == null ? "" : model.getApiKey();
                // 解密 req_info
                String req_info = reMap.get("req_info");
                String req_infoXML =UtilWechat.decryptData(req_info,merchanKey);
                //这里包含具体的退款信息内容
                Map<String,String> reqInfoMap  =UtilWechat.xmlToMap(req_infoXML);
                //并且要求退款单号相等，应用号相等
                if(model.getAppID().equalsIgnoreCase(reMap.get("appid"))
                        && refund.getRefundOrderId().equalsIgnoreCase(reqInfoMap.get("out_refund_no"))
                ){
                    Refund newRefund = new Refund();
                    newRefund.setRefundOrderId(refund.getRefundOrderId());
                    newRefund.setStatusUpdateDatetime(new Date());
                    newRefund.setUpdateAt(new Date());
                    byte ps = refund.getProcessResult();
                    if(WechatService.SUCCESS.equalsIgnoreCase(reqInfoMap.get("refund_status"))){
                        //退款成功
                        ps = EnumRefundStatus.Success.getCode();
                        int s  = new BigDecimal(reqInfoMap.getOrDefault("settlement_refund_fee","0") ).intValue() ;
                        newRefund.setFee(s);
                        if (StringUtils.isNotEmpty(reMap.get("success_time"))) {
                            //2017-12-15 09:46:01
                            newRefund.setStatusUpdateDatetime(DateUtils.simpleDatetimeFormatter().parse(reqInfoMap.get("success_time")));
                        }
                    }else if("CHANGE".equalsIgnoreCase(reqInfoMap.get("refund_status"))){
                        //退款异常 做失败处理
                        ps = EnumRefundStatus.Failed.getCode();
                    }else if("REFUNDCLOSE".equalsIgnoreCase(reqInfoMap.get("refund_status"))){
                        // 退款关闭 做失败处理
                        ps = EnumRefundStatus.Failed.getCode();
                    }
                    newRefund.setProcessResult(ps);
                    //更新refund
                   int x = refundService.updateRefund(refund, newRefund);
//                     发送通知
                    if (x == 1 && newRefund.getProcessResult().byteValue()!=refund.getProcessResult().byteValue()) {
                        RefundFutrue refundFutrue = new RefundFutrue(refund.getRefundOrderId(),refundService,orderService
                                ,appService,rabbitTemplate);
                        refundFutrue.afterFetchRefundResult(false);
                        mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                        mm.put("return_msg", "success");
                        notifyAccept.setResponstr("退款状态更新并发送通知");
                        httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                        return;
                    }else{
                        mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                        mm.put("return_msg", "update error");
                        notifyAccept.setResponstr("退款状态更新出错");
                        httpServletResponse.getOutputStream().print( UtilWechat.mapToXml(mm));
                        log.error("【{}】退款状态更新出错", refund.getRefundOrderId());
                        return;
                    }
                }else{
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "非后台通知");
                    notifyAccept.setResponstr("非后台通知或者appid不等于应用号或者退款号错误");
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
            redisLockHelper.releaseLock(RedisLockHelper.Key.RefundNotify + refundOrderId);
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
            Thread.sleep(RandomUtils.nextInt(500, 1200));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return orderService.syncUrl(orderId, httpServletRequest, httpServletResponse);
    }


    @NeedLogin(value = false)
    @RequestMapping("return/h5jump/{orderId:^\\d{21}$}")
    public ModelAndView h5jump(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                               @PathVariable String orderId, @RequestParam String param ) throws Exception {
        ModelAndView model = new ModelAndView("wechat/h5jump");
        model.addObject("surePay",domain+"/payment/wechat/return/"+orderId);
        Order order = orderService.loadByOrderID(orderId);
        String paramD =new String( UtilBase64.decode(param));
        JSONObject jo  = JSONObject.parseObject(paramD);

        model.addObject("backUrl",jo.getString("backUrl"));
        model.addObject("autoCommit",jo.getString("autoCommit"));
        model.addObject("cancelUrl",jo.getString("cancelUrl"));
       return  model;
    }
}
