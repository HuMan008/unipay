package cn.gotoil.unipay.web.controller.notify;

import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.futrue.RefundFutrue;
import cn.gotoil.unipay.model.ChargeWechatV2Model;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.NotifyAccept;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.utils.*;
import cn.gotoil.unipay.web.controller.notify.wechatnotify.model.PayNotifyModel;
import cn.gotoil.unipay.web.helper.RedissonLockHelper;
import cn.gotoil.unipay.web.helper.WechatClientHelper;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ??????????????????
 *
 * @author think <syj247@qq.com>???
 * @date 2019-9-27???11:48
 */
@Slf4j
@RequestMapping("payment/wechat/v2")
@Controller
public class WechatV2NotifyController {


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


    /**
     * ????????????????????????
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param orderId
     * @throws Exception
     */
    @RequestMapping(value = {"{orderId:^\\d{21}$}"})
    @NeedLogin(value = false)
    public void asyncNotify(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                            @PathVariable String orderId, @RequestBody String body) throws Exception {
        String wechatpaySignature = httpServletRequest.getHeader("Wechatpay-Signature");
        String wechatpaySerial = httpServletRequest.getHeader("Wechatpay-Serial");
        String wechatpayTimeStamp = httpServletRequest.getHeader("Wechatpay-TimeStamp");
        String wechatpayNonce = httpServletRequest.getHeader("Wechatpay-Nonce");
        log.debug("Wechatpay-Signature???\t{}", wechatpaySignature);
        log.debug("wechatpaySerial???\t{}", wechatpaySerial);
        log.debug("wechatpayTimeStamp???\t{}", wechatpayTimeStamp);
        log.debug("wechatpayNonce???\t{}", wechatpayNonce);
        log.debug("------");
        //        String rb = ReadAsChars(httpServletRequest);
        //        log.debug(rb);
        log.debug("------");
        PayNotifyModel payNotifyModel = JSON.parseObject(body, PayNotifyModel.class);
        NotifyAccept notifyAccept = NotifyAcceptService.createDefault(httpServletRequest, EnumOrderMessageType.PAY,
                orderId);
        notifyAccept.setOrderId(orderId);
        JSONObject mm = new JSONObject();
        try {
            //????????????????????????????????????
            if (RedissonLockHelper.isLocked(RedissonLockHelper.Key.Notify + orderId)) {
                mm.put("code", "FAIL");
                mm.put("message", "OrderProcessing");
                notifyAccept.setResponstr("FAIL:???????????????");
                httpServletResponse.getOutputStream().print(JSONObject.toJSONString(mm));
                return;
            }
            boolean r = RedissonLockHelper.tryLock(RedissonLockHelper.Key.Notify + orderId);
            if (!r) {
                return;
            }

            notifyAccept.setParams(JSON.toJSONString(payNotifyModel));

            log.info("?????????????????????{}???\n{} \n{}", orderId, JSONObject.toJSONString(payNotifyModel), wechatpaySignature);

            //????????????
            Order order = orderService.loadByOrderID(orderId);
            if (order == null) {
                mm.put("code", WechatService.FAIL);
                mm.put("message", "OrderNotExists");
                notifyAccept.setResponstr("FAIL:???????????????");
                httpServletResponse.getOutputStream().print(JSON.toJSONString(mm));
                return;
            }
            notifyAccept.setAppOrderNo(order.getAppOrderNo());
            //???????????????????????????
            if (EnumOrderStatus.PaySuccess.getCode() == order.getStatus().byteValue()) {
                mm.put("code", WechatService.SUCCESS);
                mm.put("message", "Doed,skip");
                notifyAccept.setResponstr("SKIP:??????????????????");
                httpServletResponse.getOutputStream().print(JSON.toJSONString(mm));
                return;
            }
            if (EnumOrderStatus.Created.getCode() != order.getStatus().byteValue()) {
                mm.put("code", WechatService.FAIL);
                mm.put("message", "LocationStatusError");
                notifyAccept.setResponstr("FAIL:???????????????????????????");
                httpServletResponse.getOutputStream().print(JSON.toJSONString(mm));
                return;
            }
            ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
            ChargeWechatV2Model model = JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                    ChargeWechatV2Model.class);
            X509Certificate certificate = WechatClientHelper.getInstance().certificate(model.getMerchId());
            String se = certificate.getSerialNumber().toString(16);
            log.debug("se-->{}", se);
            //z?????????????????????????????? ????????????
            if (se.equalsIgnoreCase(wechatpaySerial)) {


                if (UtilWechat.verify(WechatClientHelper.getInstance().certificate(model.getMerchId()),
                        Stream.of(wechatpayTimeStamp, wechatpayNonce, body).collect(Collectors.joining("\n", "",
                                "\n")), wechatpaySignature)) {

                    AesUtil aesUtil = new AesUtil(model.getApiKeyV3().getBytes());
                    String josnStr =
                            aesUtil.decryptToString(payNotifyModel.getResource().getAssociated_data().getBytes(),
                                    payNotifyModel.getResource().getNonce().getBytes(),
                                    payNotifyModel.getResource().getCiphertext());
                    log.info(josnStr);
                    JSONObject jo = JSON.parseObject(josnStr);
                    //{"mchid":"1570277731","appid":"wx38e59840a2f7a3b5","out_trade_no":"202101121111263798536",
                    // "transaction_id":"4200000917202101123071101432","trade_type":"APP","trade_state":"SUCCESS",
                    // "trade_state_desc":"????????????","bank_type":"BOC_DEBIT","attach":"",
                    // "success_time":"2021-01-12T11:13:31+08:00","payer":{"openid":"oZYrct177fisFk5nJ6Z9OTlRs4ss"},
                    // "amount":{"total":1,"payer_total":1,"currency":"CNY","payer_currency":"CNY"}}

                    if (WechatService.SUCCESS.equalsIgnoreCase(jo.getString("trade_state"))) {
                        //                        ????????????
                        Order newOrder = new Order();
                        newOrder.setId(order.getId());
                        newOrder.setPayFee(jo.getJSONObject("amount").getInteger("payer_total"));
                        newOrder.setArrFee(jo.getJSONObject("amount").getInteger("total"));
                        newOrder.setStatus(EnumOrderStatus.PaySuccess.getCode());
                        if (StringUtils.isNotEmpty(jo.getString("success_time"))) {
                            //yyyyMMddHHmmss
                            newOrder.setOrderPayDatetime(DateUtils.fetchSimpleDateFormatter(DateUtil.RFC3339).parse(jo.getString("success_time")).getTime() / 1000);
                        } else {
                            newOrder.setOrderPayDatetime(0L);
                        }
                        newOrder.setPaymentUid(jo.getJSONObject("payer").getString("openid"));
                        newOrder.setPaymentId(jo.getString("transaction_id"));
                        notifyAccept.setPaymentId(newOrder.getPaymentId());
                        int x = orderService.updateOrder(order, newOrder);
                        if (x != 1) {
                            mm.put("code", WechatService.FAIL);
                            mm.put("message", "LocationUpdateFail");
                            notifyAccept.setResponstr("FAIL:??????????????????????????????");
                            httpServletResponse.getOutputStream().print(JSON.toJSONString(mm));
                            log.info("???????????????{}????????????????????????????????????????????????", orderId);
                            return;
                        } else {
                            log.info("???????????????{}????????????????????????????????????????????????", orderId);
                            OrderNotifyBean orderNotifyBean =
                                    OrderNotifyBean.builder().unionOrderID(order.getId()).appId(order.getAppId()).method(EnumOrderMessageType.PAY.name()).appOrderNO(order.getAppOrderNo()).status(newOrder.getStatus()).orderFee(order.getFee()).paymentId(newOrder.getPaymentId()).payFee(newOrder.getPayFee()).asyncUrl(order.getAsyncUrl()).extraParam(order.getExtraParam()).payDate(newOrder.getOrderPayDatetime()).arrFee(newOrder.getArrFee()).timeStamp(Instant.now().getEpochSecond()).build();
                            String appSecret = appService.key(order.getAppId());
                            String signStr = UtilMySign.sign(orderNotifyBean, appSecret);
                            orderNotifyBean.setSign(signStr);
                            rabbitTemplate.convertAndSend(ConstsRabbitMQ.ORDERFIRSTEXCHANGENAME,
                                    ConstsRabbitMQ.ORDERROUTINGKEY, JSON.toJSONString(orderNotifyBean));

                            mm.put("code", WechatService.SUCCESS);
                            mm.put("message", "OK");
                            notifyAccept.setResponstr("OK:???????????????");
                            httpServletResponse.getOutputStream().print(JSON.toJSONString(mm));
                            log.info("?????????????????????{}??????????????????????????????SUCCESS,??????????????????", orderId);
                            return;
                        }
                    } else {
                        //  ??????????????????
                        mm.put("code", WechatService.SUCCESS);
                        mm.put("message", "NotifyStatusNotSuccess");
                        notifyAccept.setResponstr("OK:??????????????????????????????");
                        httpServletResponse.getOutputStream().print(JSON.toJSONString(mm));
                        return;
                    }


                } else {
                    mm.put("code", WechatService.FAIL);
                    mm.put("message", "????????????");
                    notifyAccept.setResponstr("FAIL:???????????????");
                    httpServletResponse.getOutputStream().print(JSONObject.toJSONString(mm));
                    log.error("??????????????? -->{} ", orderId);
                    return;
                }

            } else {
                mm.put("code", WechatService.FAIL);
                mm.put("message", "???????????????????????????????????????");
                notifyAccept.setResponstr("FAIL:???????????????????????????????????????");
                httpServletResponse.getOutputStream().print(JSONObject.toJSONString(mm));
                log.error("?????????{}??? --???????????????????????????????????????-- ", model.getMerchId());
                return;
            }




            /*if (reMap.containsKey(WechatService.RETURN_CODE) && WechatService.SUCCESS.equals(reMap.get
            (WechatService.RETURN_CODE).toString())) {
                //??????????????????????????????????????????????????????????????????????????????????????????
                if (!orderId.equals(reMap.get("out_trade_no"))) {
                    mm.put("code", WechatService.FAIL);
                    mm.put("message", "OrderNOError");
                    notifyAccept.setResponstr("FAIL:???????????????");
                    httpServletResponse.getOutputStream().print(JSONObject.toJSONString(mm));
                    return ;
                }




                //???????????? APP NATIVATE JSAPI
                String trade_type = reMap.get("trade_type");
                //?????????????????????????????????????????????


                String merchanKey = model == null ? "" : model.getApiKey();
                if (UtilWechat.isSignatureValid(reMap, merchanKey) && model.getAppID().equalsIgnoreCase(reMap.get(
                        "appid"))) {
                    //????????????
                    if (WechatService.SUCCESS.equalsIgnoreCase(reMap.get(WechatService.RESULT_CODE))) {
                        //                        ????????????
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
                            mm.put("code", WechatService.FAIL);
                            mm.put("message", "LocationUpdateFail");
                            notifyAccept.setResponstr("FAIL:??????????????????????????????");
                            httpServletResponse.getOutputStream().print(JSON.toJSONString(mm));
                            log.info("???????????????{}????????????????????????????????????????????????", orderId);
                            return;
                        } else {
                            log.info("???????????????{}????????????????????????????????????????????????", orderId);
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

                            mm.put("code", WechatService.SUCCESS);
                            mm.put("message", "OK");
                            notifyAccept.setResponstr("OK:???????????????");
                            httpServletResponse.getOutputStream().print(JSON.toJSONString(mm));
                            log.info("?????????????????????{}??????????????????????????????SUCCESS,??????????????????", orderId);
                            return ;
                        }
                    } else {
                        //  ??????????????????
                        mm.put("code", WechatService.SUCCESS);
                        mm.put("message", "NotifyStatusNotSuccess");
                        notifyAccept.setResponstr("OK:??????????????????????????????");
                        httpServletResponse.getOutputStream().print( JSON.toJSONString(mm));
                        return;
                    }
                } else {
                    mm.put("code", WechatService.FAIL);
                    mm.put("message", "VerifyError");
                    notifyAccept.setResponstr("FAIL:??????????????????");
                    httpServletResponse.getOutputStream().print( JSON.toJSONString(mm));
                    return ;
                }
            } else {
                mm.put("code", WechatService.FAIL);
                mm.put("message", "MessageError");
                notifyAccept.setResponstr("FAIL:????????????????????????");
                httpServletResponse.getOutputStream().print( JSON.toJSONString(mm));
                return ;
            }*/
        } catch (Exception e) {
            log.error("????????????????????????????????????{}", e);
            mm.put("code", WechatService.FAIL);
            mm.put("message", e.getMessage());
            notifyAccept.setResponstr(UtilString.getLongString("FAIL:" + e.getMessage(), 4000));
            httpServletResponse.getOutputStream().print(JSON.toJSONString(mm));
            return;

        } finally {
            RedissonLockHelper.unlock(RedissonLockHelper.Key.Notify + orderId);

            notifyAcceptService.add(notifyAccept);
        }
    }

    /**
     * @param httpServletRequest
     * @param httpServletResponse
     * @param orderId             ?????????????????????
     * @param refundOrderId       ?????????????????????
     * @throws Exception
     */
    @RequestMapping(value = {"refund/{orderId:^\\d{21}$}/{refundOrderId}"})
    @NeedLogin(value = false)
    public void asyncRefundNotify(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                  @PathVariable String orderId, @PathVariable String refundOrderId) throws Exception {

        httpServletResponse.setCharacterEncoding(Charsets.ISO_8859_1.name());
        String requestBodyXml = "";//??????xml
        Map<String, String> mm = new HashMap<>();
        NotifyAccept notifyAccept = NotifyAcceptService.createDefault(httpServletRequest, EnumOrderMessageType.REFUND
                , orderId);
        try {
            //????????????????????????????????????
            if (RedissonLockHelper.isLocked(RedissonLockHelper.Key.RefundNotify + refundOrderId)) {
                mm.put(WechatService.RETURN_CODE, "FAIL");
                mm.put("return_msg", "OrderProcessing");
                notifyAccept.setResponstr("FAIL:?????????????????????");
                httpServletResponse.getOutputStream().print(UtilWechat.mapToXml(mm));
                return;
            }
            boolean r = RedissonLockHelper.tryLock(RedissonLockHelper.Key.RefundNotify + refundOrderId);
            if (!r) {
                return;
            }
            requestBodyXml = CharStreams.toString(httpServletRequest.getReader());
            notifyAccept.setParams(requestBodyXml);
            //??????XML??????Map
            Map<String, String> reMap = UtilWechat.xmlToMap(requestBodyXml);

            log.info("???????????????????????????{}???\n{}", refundOrderId, JSONObject.toJSONString(reMap));
            //??????code ???success
            if (reMap.containsKey(WechatService.RETURN_CODE) && WechatService.SUCCESS.equals(reMap.get(WechatService.RETURN_CODE).toString())) {

                //??????????????????
                Refund refund = refundService.loadById(refundOrderId);
                if (refund == null) {
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "RefundNotExists");
                    notifyAccept.setResponstr("FAIL:?????????????????????");
                    httpServletResponse.getOutputStream().print(UtilWechat.mapToXml(mm));
                    return;
                }
                //????????????
                Order order = orderService.loadByOrderID(refund.getOrderId());
                if (order == null) {
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "OrderNotExists");
                    notifyAccept.setResponstr("FAIL:?????????????????????");
                    httpServletResponse.getOutputStream().print(UtilWechat.mapToXml(mm));
                    return;
                }
                notifyAccept.setAppOrderNo(order.getAppOrderNo());

                //???????????????????????????
                if (EnumRefundStatus.WaitSure.getCode() != refund.getProcessResult().byteValue()) {
                    mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                    mm.put("return_msg", "success");
                    notifyAccept.setResponstr("SKIP:??????????????????");
                    httpServletResponse.getOutputStream().print(UtilWechat.mapToXml(mm));
                    return;
                }
                //?????????????????????????????????????????????
                ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
                ChargeWechatV2Model model = JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                        ChargeWechatV2Model.class);
                String merchanKey = model == null ? "" : model.getApiKeyV2();
                // ?????? req_info
                String req_info = reMap.get("req_info");
                String req_infoXML = UtilWechat.decryptData(req_info, merchanKey);
                //???????????????????????????????????????
                Map<String, String> reqInfoMap = UtilWechat.xmlToMap(req_infoXML);
                //????????????????????????????????????????????????
                if (model.getAppId().equalsIgnoreCase(reMap.get("appid")) && refund.getRefundOrderId().equalsIgnoreCase(reqInfoMap.get("out_refund_no"))) {
                    Refund newRefund = new Refund();
                    newRefund.setRefundOrderId(refund.getRefundOrderId());
                    newRefund.setStatusUpdateDatetime(new Date());
                    newRefund.setUpdateAt(new Date());
                    byte ps = refund.getProcessResult();
                    if (WechatService.SUCCESS.equalsIgnoreCase(reqInfoMap.get("refund_status"))) {
                        //????????????
                        ps = EnumRefundStatus.Success.getCode();
                        int s = new BigDecimal(reqInfoMap.getOrDefault("settlement_refund_fee", "0")).intValue();
                        newRefund.setFee(s);
                        if (StringUtils.isNotEmpty(reMap.get("success_time"))) {
                            //2017-12-15 09:46:01
                            newRefund.setStatusUpdateDatetime(DateUtils.simpleDatetimeFormatter().parse(reqInfoMap.get("success_time")));
                        }
                    } else if ("CHANGE".equalsIgnoreCase(reqInfoMap.get("refund_status"))) {
                        //???????????? ???????????????
                        ps = EnumRefundStatus.Failed.getCode();
                    } else if ("REFUNDCLOSE".equalsIgnoreCase(reqInfoMap.get("refund_status"))) {
                        // ???????????? ???????????????
                        ps = EnumRefundStatus.Failed.getCode();
                    }
                    newRefund.setProcessResult(ps);
                    //??????refund
                    int x = refundService.updateRefund(refund, newRefund);
                    //                     ????????????
                    if (x == 1 && newRefund.getProcessResult().byteValue() != refund.getProcessResult().byteValue()) {
                        RefundFutrue refundFutrue = new RefundFutrue(refund.getRefundOrderId(), refundService,
                                orderService, appService, rabbitTemplate);
                        refundFutrue.afterFetchRefundResult(false);
                        mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                        mm.put("return_msg", "success");
                        notifyAccept.setResponstr("?????????????????????????????????");
                        httpServletResponse.getOutputStream().print(UtilWechat.mapToXml(mm));
                        return;
                    } else {
                        mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                        mm.put("return_msg", "update error");
                        notifyAccept.setResponstr("????????????????????????");
                        httpServletResponse.getOutputStream().print(UtilWechat.mapToXml(mm));
                        log.error("???{}???????????????????????????", refund.getRefundOrderId());
                        return;
                    }
                } else {
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "???????????????");
                    notifyAccept.setResponstr("?????????????????????appid???????????????????????????????????????");
                    httpServletResponse.getOutputStream().print(UtilWechat.mapToXml(mm));
                    return;
                }
            } else {
                mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                mm.put("return_msg", "MessageError");
                notifyAccept.setResponstr("FAIL:????????????????????????");
                httpServletResponse.getOutputStream().print(UtilWechat.mapToXml(mm));
                return;
            }
        } catch (Exception e) {
            log.error("????????????????????????????????????{}", e);
            mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
            mm.put("return_msg", e.getMessage());
            notifyAccept.setResponstr(UtilString.getLongString("FAIL:" + e.getMessage(), 4000));
            httpServletResponse.getOutputStream().print(UtilWechat.mapToXml(mm));
            return;

        } finally {
            RedissonLockHelper.unlock(RedissonLockHelper.Key.RefundNotify + refundOrderId);
            notifyAcceptService.add(notifyAccept);
        }
    }

    @NeedLogin(value = false)
    @RequestMapping("return/{orderId:^\\d{21}$}")
    public ModelAndView syncNotify(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                   @PathVariable String orderId) throws Exception {
        log.debug("??????????????????{}", orderId);
        //        //???????????????????????????
        //        try {
        //            Thread.sleep(RandomUtils.nextInt(500, 1200));
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
        return orderService.syncUrl(orderId, httpServletRequest, httpServletResponse);
    }

    @NeedLogin(value = false)
    @RequestMapping("return/h5jump/{orderId:^\\d{21}$}")
    public ModelAndView h5jump(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                               @PathVariable String orderId, @RequestParam String param) throws Exception {
        ModelAndView model = new ModelAndView("wechat/h5jump");
        model.addObject("surePay", domain + "/payment/wechat/return/" + orderId);
        Order order = orderService.loadByOrderID(orderId);
        String paramD = new String(UtilBase64.decode(param));
        JSONObject jo = JSONObject.parseObject(paramD);

        model.addObject("backUrl", jo.getString("backUrl"));
        model.addObject("autoCommit", jo.getString("autoCommit"));
        model.addObject("cancelUrl", jo.getString("cancelUrl"));
        return model;
    }
}
