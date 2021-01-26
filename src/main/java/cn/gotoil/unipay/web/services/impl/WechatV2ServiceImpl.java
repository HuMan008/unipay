package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeWechatV2Model;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.utils.*;
import cn.gotoil.unipay.web.helper.WechatClientHelper;
import cn.gotoil.unipay.web.message.request.ContinuePayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.message.response.OrderRefundResponse;
import cn.gotoil.unipay.web.message.response.RefundQueryResponse;
import cn.gotoil.unipay.web.services.OrderService;
import cn.gotoil.unipay.web.services.RefundService;
import cn.gotoil.unipay.web.services.WechatService;
import cn.gotoil.unipay.web.services.WechatV2Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author think <syj247@qq.com>、
 * @date 2021/1/6、11:17
 */
@Service
@Slf4j
public class WechatV2ServiceImpl implements WechatV2Service {

    @Value("${domain}")
    String domain;

    @Autowired
    RefundService refundService;
    @Resource
    OrderService orderService;

    /**
     * 页面支付
     *
     * @param order
     * @param chargeModel
     * @param httpServletRequest
     * @param httpServletResponse
     * @param continuePayRequest
     * @param needSave
     * @return
     */
    @Override
    public ModelAndView pagePay(Order order, ChargeWechatV2Model chargeModel, HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, ContinuePayRequest continuePayRequest,
                                boolean needSave) {
        try {
            JSONObject data = warpCreateOrderPostMap(order, chargeModel, order.getPaymentUid());

            HttpPost httpPost = new HttpPost(createOrderUrl(order));
            StringEntity entity = new StringEntity(JSON.toJSONString(data), "utf-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            CloseableHttpResponse response = WechatClientHelper.getInstance().client(chargeModel).execute(httpPost);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String returnStr = EntityUtils.toString(response.getEntity());
                    if (needSave) {
                        int x = orderService.saveOrder(order);
                        if (x != 1) {
                            return new ModelAndView(UtilPageRedirect.makeErrorPage(UnipayError.PageRefreshError,
                                    continuePayRequest.getBackUrl()));
                        }
                    }
                    //jsapi
                    if (EnumPayType.WechatJSAPI.getCode().equals(order.getPayType())) {
                        long timestamp = Instant.now().getEpochSecond();
                        String nonceStr = RandomStringUtils.random(16, true, true);

                        ModelAndView modelAndView = new ModelAndView("wechat/jsapipay");

                        Map<String, String> ssMap = new HashMap();
                        ssMap.put("appId", chargeModel.getAppId());
                        ssMap.put("timeStamp", String.valueOf(timestamp));
                        ssMap.put("nonceStr", nonceStr);
                        ssMap.put("package", "prepay_id=" + JSON.parseObject(returnStr).get("prepay_id"));
                        ssMap.put("signType", "RSA"); //固定值
                        String sign = UtilWechat.sign(Stream.of(chargeModel.getAppId(), String.valueOf(timestamp),
                                nonceStr, ssMap.get("package")).collect(Collectors.joining("\n", "", "\n")),
                                WechatClientHelper.getInstance().privateKey(chargeModel.getMerchId()));


                        ssMap.put("paySign", sign);
                        modelAndView.addAllObjects(ssMap);
                        fillWechatPage(modelAndView, order, continuePayRequest.getCancelUrl(),
                                continuePayRequest.getBackUrl(), continuePayRequest.getAutoCommit());

                        return modelAndView;
                    } else {
                        // H5
                        JSONObject jo = JSONObject.parseObject(returnStr);
                        httpServletResponse.sendRedirect(jo.getString("h5_url"));
                        return null;
                    }


                } else {
                    log.error("微信V3 page 统一下单错:\t{}-->{}", statusCode, EntityUtils.toString(response.getEntity()));
                    return new ModelAndView(UtilPageRedirect.makeErrorPage(5001,
                            statusCode + ":" + EntityUtils.toString(response.getEntity()),
                            continuePayRequest.getBackUrl()));
                }
            } finally {
                response.close();

            }
        } catch (Exception e) {
            log.info("微信订单创建错误{}", e.getMessage());
        }
        return null;
    }


    private void fillWechatPage(ModelAndView modelAndView, Order order, String cancelUrl, String backUrl,
                                int autoCommit) {
        modelAndView.addObject("domain", domain);
        modelAndView.addObject("appOrderNo", order.getAppOrderNo());
        modelAndView.addObject("orderId", order.getId());
        modelAndView.addObject("cancelUrl", cancelUrl);
        modelAndView.addObject("backUrl", backUrl);
        modelAndView.addObject("successUrl", domain + "/payment/wechat/return/" + order.getId());
        modelAndView.addObject("subject", order.getSubjects());
        modelAndView.addObject("descp", order.getDescp());
        modelAndView.addObject("autoCommit", "1");
        modelAndView.addObject("orderFeeY", UtilMoney.fenToYuan(order.getFee(), false));
    }


    /**
     * SDK 支付 返回JSON
     *
     * @param order
     * @param chargeModel
     * @return
     */
    @Override
    public String sdkPay(Order order, ChargeWechatV2Model chargeModel) {
        try {
            JSONObject data = warpCreateOrderPostMap(order, chargeModel, "");
            HttpPost httpPost = new HttpPost(createOrderUrl(order));
            StringEntity entity = new StringEntity(JSON.toJSONString(data), Charset.forName("utf-8"));
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            CloseableHttpResponse response = WechatClientHelper.getInstance().client(chargeModel).execute(httpPost);
            try {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    String retStr = EntityUtils.toString(response.getEntity());
                    JSONObject retJo = JSON.parseObject(retStr);
                    JSONObject jo = new JSONObject();
                    jo.put("appid", chargeModel.getAppId());
                    jo.put("partnerid", chargeModel.getMerchId());
                    jo.put("prepayid", retJo.getString("prepay_id"));
                    jo.put("package", retJo.getString("Sign=WXPay"));
                    jo.put("noncestr", UtilWechat.generateNonceStr());
                    jo.put("timestamp", String.valueOf(Instant.now().getEpochSecond()));
                    String sign;
                    sign = UtilWechat.sign(Stream.of(jo.getString("appid"), jo.getString("timestamp"), jo.getString(
                            "noncestr"), jo.getString("prepayid")).collect(Collectors.joining("\n", "", "\n")),
                            WechatClientHelper.getInstance().privateKey(jo.getString("partnerid")));
                    jo.put("sign", sign);
                    return jo.toJSONString();
                } else {
                    log.error("微信V3 SDK 统一下单错:\t{}-->{}", statusCode, EntityUtils.toString(response.getEntity()));
                    return "";
                }
            } finally {
                response.close();

            }

        } catch (Exception e) {
            log.info("微信订单创建错误{}", e.getMessage());
        }
        return null;
    }


    private JSONObject warpCreateOrderPostMap(Order order, ChargeWechatV2Model chargeModel, String openId) {
        JSONObject data = new JSONObject();
        data.put("appid", chargeModel.getAppId());
        data.put("mchid", chargeModel.getMerchId());
        data.put("description", UtilString.getLongString(StringUtils.isEmpty(order.getSubjects()) ? "empty" + " " +
                "description" : order.getSubjects(), 128));
        data.put("out_trade_no", order.getId());
        data.put("time_expire",
                DateUtil.fetchSimpleDateFormatter(DateUtil.RFC3339).format(DateUtils.dateAdd(order.getCreatedAt(), 0,
                        0, 0, 0, order.getExpiredTimeMinute(), 0)));
        data.put("attach", order.getExtraParam());
        data.put("notify_url", domain + "/payment/wechat/v2/" + order.getId());

        JSONObject amount = new JSONObject();
        amount.put("currency", "CNY");
        amount.put("total", order.getFee());
        data.put("amount", amount);
        if (StringUtils.isNotEmpty(openId)) {
            JSONObject payer = new JSONObject();
            payer.put("openid", openId);
            data.put("payer", payer);
        }
        return data;
    }


    /**
     * 订单支付状态查询 远程查
     * https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_3_2.shtml#menu2
     *
     * @param order
     * @param chargeConfig
     */
    @Override
    public OrderQueryResponse orderQueryFromRemote(Order order, ChargeWechatV2Model chargeConfig) {
        String msg = "";
        try {
            URIBuilder uriBuilder = new URIBuilder(WechatV2Service.QUERY_ORDER_URL + order.getId());
            uriBuilder.setParameter("mchid", chargeConfig.getMerchId());
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("Accept", "application/json");
            CloseableHttpResponse response = WechatClientHelper.getInstance().client(chargeConfig).execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
                if (jsonObject != null) {
                    OrderQueryResponse orderQueryResponse =
                            OrderQueryResponse.builder().unionOrderID(jsonObject.getString("out_trade_no")).appOrderNO(order.getAppOrderNo()).paymentId(jsonObject.getString("transaction_id")).paymentUid(jsonObject.getJSONObject("payer") == null ? "" : jsonObject.getJSONObject("payer").getString("openid")).payDateTime(0).orderFee(order.getFee()).payFee(0).arrFee(0).thirdStatus(jsonObject.getString("trade_state")).thirdCode(jsonObject.getString("trade_state_desc")).thirdMsg(statusCode + " :" + jsonObject.getString("trade_state") + "#" + jsonObject.getString("trade_state_desc")).build();
                    ;
                /*      SUCCESS—支付成功
                        REFUND—转入退款
                        NOTPAY—未支付
                        CLOSED—已关闭
                        REVOKED—已撤销（付款码支付）
                        USERPAYING--用户支付中（付款码支付）
                        PAYERROR--支付失败(其他原因，如银行返回失败)*/
                    if ("SUCCESS".equalsIgnoreCase(jsonObject.getString("trade_state"))) {
                        orderQueryResponse.setStatus(EnumOrderStatus.PaySuccess.getCode());
                        try {
                            orderQueryResponse.setPayDateTime(DateUtils.fetchSimpleDateFormatter(DateUtil.RFC3339).parse(jsonObject.getString("success_time")).getTime() / 1000);
                        } catch (ParseException e) {
                            log.error("支付日期解析出错 -->{}", jsonObject.getString("success_time"));
                        }
                        JSONObject amount = jsonObject.getJSONObject("amount");
                        orderQueryResponse.setPayFee(amount.getInteger("total"));
                        orderQueryResponse.setArrFee(amount.getInteger("payer_total"));
                    } else if ("NOTPAY".equalsIgnoreCase(jsonObject.getString("trade_state")) || "USERPAYING".equalsIgnoreCase(jsonObject.getString("trade_state"))) {
                        Date flagDate = org.apache.commons.lang3.time.DateUtils.addMinutes(order.getCreatedAt(),
                                order.getExpiredTimeMinute() + 30);
                        if (flagDate.getTime() < System.currentTimeMillis()) {
                            orderQueryResponse.setStatus(EnumOrderStatus.PayFailed.getCode());
                        } else {
                            orderQueryResponse.setStatus(EnumOrderStatus.Created.getCode());
                        }
                    } else if ("PAYERROR".equalsIgnoreCase(jsonObject.getString("trade_state")) || "CLOSED".equalsIgnoreCase(jsonObject.getString("trade_state"))) {
                        orderQueryResponse.setStatus(EnumOrderStatus.PayFailed.getCode());
                    }
                    return orderQueryResponse;
                } else {
                    msg = statusCode + EntityUtils.toString(response.getEntity());
                }
            } else {
                log.error("微信V3订单查询出错:\t{}-->{}", statusCode, EntityUtils.toString(response.getEntity()));
                msg = statusCode + EntityUtils.toString(response.getEntity());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return OrderQueryResponse.builder().appOrderNO(order.getAppOrderNo()).thirdCode("5000").thirdMsg(msg).build();
    }

    /**
     * 退款申请
     * V3 与V2 是一样的
     *
     * @param chargeModel
     * @param refund
     * @return
     */
    @Override
    public OrderRefundResponse orderRefund(ChargeWechatV2Model chargeModel, Refund refund) {
        Refund newRefund = new Refund();
        newRefund.setRefundOrderId(refund.getRefundOrderId());
        try {
            File certFile = new File(chargeModel.getCertPath());
            if (StringUtils.isEmpty(chargeModel.getCertPath()) || !certFile.isFile() || !certFile.exists()) {

                newRefund.setProcessResult(EnumRefundStatus.Failed.getCode());
                newRefund.setFailMsg(UnipayError.RefundError_NoCertPath.getMessage());
                throw new BillException(UnipayError.RefundError_NoCertPath);
            }

            HashMap<String, String> data = new HashMap<String, String>();
            data.put("appid", chargeModel.getAppId());
            data.put("mch_id", chargeModel.getMerchId());
            data.put("nonce_str", UtilWechat.generateNonceStr());
            data.put("out_refund_no", refund.getRefundOrderId());
            data.put("out_trade_no", refund.getOrderId());
            data.put("total_fee", String.valueOf(refund.getOrderFee()));
            data.put("refund_fee", String.valueOf(refund.getApplyFee()));
            data.put("refund_desc", refund.getDescp());
            data.put("notify_url", domain + "/payment/wechat/v2/refund/" + refund.getOrderId() + "/" + refund.getRefundOrderId());
            String sign = "";
            try {
                sign = UtilWechat.generateSignature(data, chargeModel.getApiKeyV2());
            } catch (Exception e) {
                log.error("微信加签错误", e.getMessage());
            }
            data.put(UtilWechat.FIELD_SIGN, sign);


            String repStr = UtilHttpClient.postConnWithCert(WechatService.RefundUrl, UtilWechat.mapToXml(data),
                    chargeModel.getCertPath(), chargeModel.getMerchId());
            Map<String, String> reMap = UtilWechat.processResponseXml(repStr, chargeModel.getApiKeyV2());
            if (reMap.containsKey("return_code") && reMap.get("return_code").equals("SUCCESS") && "SUCCESS".equals(reMap.getOrDefault("return_code", ""))) {
                // 提交对了
                newRefund.setStatusUpdateDatetime(new Date());
                newRefund.setProcessResult(EnumRefundStatus.WaitSure.getCode());
                newRefund.setUpdateAt(refund.getStatusUpdateDatetime());
                OrderRefundResponse orderRefundResponse = new OrderRefundResponse();
                orderRefundResponse.setRefundStatus(EnumRefundStatus.Refunding.getCode());
                orderRefundResponse.setMsg("退款申请成功，请调用查询退款获取退款结果");
                orderRefundResponse.setReslutQueryId(refund.getRefundOrderId());
                return orderRefundResponse;
            } else {
                newRefund.setFailMsg(UtilString.getLongString(reMap.get("return_msg"), 199));
                OrderRefundResponse orderRefundResponse = new OrderRefundResponse();
                orderRefundResponse.setRefundStatus(EnumRefundStatus.Refunding.getCode());
                orderRefundResponse.setReslutQueryId(refund.getRefundOrderId());
                newRefund.setProcessResult(EnumRefundStatus.Failed.getCode());
                orderRefundResponse.setMsg("退款申请失败:" + reMap.get("return_msg"));
                return orderRefundResponse;
            }
        } catch (Exception e) {
            throw new BillException(55, e.getMessage());
        } finally {
            refundService.updateRefund(refund, newRefund);
        }


    }

    /**
     * 退款状态查询
     *
     * @param chargeModel
     * @param refund
     * @return
     */
    @Override
    public RefundQueryResponse orderRefundQuery(ChargeWechatV2Model chargeModel, Refund refund) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("appid", chargeModel.getAppId());
        data.put("mch_id", chargeModel.getMerchId());
        data.put("nonce_str", UtilWechat.generateNonceStr());
        data.put("out_refund_no", refund.getRefundOrderId());
        String sign = "";
        try {
            sign = UtilWechat.generateSignature(data, chargeModel.getApiKeyV2());
        } catch (Exception e) {
            log.error("微信加签错误", e.getMessage());
        }
        data.put(UtilWechat.FIELD_SIGN, sign);
        try {
            String repStr = UtilHttpClient.doPostStr(WechatService.RefundQueryUrl, UtilWechat.mapToXml(data));
            Map<String, String> reMap = UtilWechat.processResponseXml(repStr, chargeModel.getApiKeyV2());
            if (reMap.containsKey("return_code") && reMap.get("return_code").equals("SUCCESS") && "SUCCESS".equals(reMap.getOrDefault("return_code", ""))) {
                RefundQueryResponse refundQueryResponse =
                        RefundQueryResponse.builder().orderRefundId(refund.getRefundOrderId()).thirdCode(reMap.get(
                                "result_code") + reMap.get("return_code")).thirdMsg(reMap.get("return_msg") + reMap.get("err_code_des")).orderId(refund.getOrderId()).appOrderNo(refund.getAppOrderNo()).appOrderRefundNo(refund.getAppOrderRefundNo()).applyFee(refund.getApplyFee()).build();

                String refundStatus = reMap.get("refund_status_0");
                 /*   SUCCESS—退款成功
                    REFUNDCLOSE—退款关闭。
                    PROCESSING—退款处理中
                    CHANGE—退款异常，*/
                EnumRefundStatus enumOrderStatus = null;
                if ("SUCCESS".equals(refundStatus) || "REFUNDCLOSE".equals(refundStatus)) {
                    enumOrderStatus = EnumRefundStatus.Success;
                    refundQueryResponse.setPassFee(Integer.parseInt(reMap.get("refund_fee_0")));
                } else if ("PROCESSING".equals(refundStatus)) {
                    enumOrderStatus = EnumRefundStatus.WaitSure;
                    refundQueryResponse.setPassFee(0);
                } else if ("CHANGE".equals(refundStatus)) {
                    enumOrderStatus = EnumRefundStatus.Failed;
                    refundQueryResponse.setPassFee(0);
                }
                refundQueryResponse.setRefundStatus(enumOrderStatus.getCode());
                return refundQueryResponse;
            } else {
                throw new BillException(5003, JSON.toJSONString(reMap));
            }
        } catch (Exception e) {
            throw new BillException(55, e.getMessage());
        }
    }


}
