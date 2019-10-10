package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.utils.UtilHttpClient;
import cn.gotoil.unipay.utils.UtilMoney;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.utils.UtilWechat;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.message.response.OrderRefundQueryResponse;
import cn.gotoil.unipay.web.services.WechatService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 微信支付实现类
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-26、15:58
 */
@Service
@Slf4j
public class WechatServiceImpl implements WechatService {
    @Value("${domain}")
    String domain;

    /**
     * 页面支付
     *
     * @param payRequest
     * @param order
     * @param chargeConfig
     * @return
     */
    @Override
    public ModelAndView pagePay(PayRequest payRequest, Order order, ChargeAccount chargeConfig,
                                HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ChargeWechatModel chargeModel = (ChargeWechatModel) chargeConfig;
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("appid", chargeModel.getAppID());
        data.put("mch_id", chargeModel.getMerchID());
        data.put("nonce_str", UtilWechat.generateNonceStr());
        data.put("body", UtilString.getLongString("[" + order.getSubjects() + "]" + order.getDescp(), 128));
        data.put("sign_type", UtilWechat.SignType.MD5.name());
        data.put("openid", payRequest.getPaymentUserID());
        data.put("out_trade_no", order.getId());
        data.put("total_fee", String.valueOf(order.getFee()));
        data.put("time_expire",
                DateUtils.simpleDateTimeNoSymbolFormatter().format(DateUtils.dateAdd(order.getCreatedAt(), 0, 0, 0, 0
                        , order.getExpiredTimeMinute(), 0)));

        data.put("notify_url", domain + "/payment/wechat/" + order.getId());
        if (EnumPayType.WechatJSAPI.getCode().equals(payRequest.getPayType())) {
            data.put("trade_type", TradeType.JSAPI.name());
        } else if (EnumPayType.WechatH5.getCode().equals(payRequest.getPayType())) {
            data.put("trade_type", TradeType.MWEB.name());
            //这里还需要放IP todo
            //http://ip138.com/ ？
            //https://ip.huomao.com/ip？
            //request里取？
            data.put("spbill_create_ip", "123.33.3.3");
        }
        String sign = "";
        try {
            sign = UtilWechat.generateSignature(data, chargeModel.getMerchKey());
        } catch (Exception e) {
            log.error("微信加签错误", e.getMessage());
            return new ModelAndView(UtilString.makeErrorPage(5000, "微信加签错误"));
        }
        data.put("sign", sign);
        try {
            String repStr = UtilHttpClient.doPostStr(WechatService.CreateOrderUrl, UtilWechat.mapToXml(data));
            Map<String, String> reMap = processResponseXml(repStr, chargeModel.getMerchKey());
            if (reMap.containsKey(RETURN_CODE) && reMap.containsKey(RESULT_CODE) && SUCCESS.equals(reMap.get(RETURN_CODE)) && SUCCESS.equals(reMap.get(RESULT_CODE))) {
                //保存订单 todo

                if (TradeType.MWEB.name().equals(reMap.get("trade_type"))) {
                    return new ModelAndView("redirect:" + reMap.get("mweb_url"));
                }
                ModelAndView modelAndView = new ModelAndView("/wechat/jsapipay");
                modelAndView.addAllObjects(reMap);
                modelAndView.addObject("domain", domain);
                modelAndView.addObject("appOrderNo", order.getAppOrderNo());
                modelAndView.addObject("orderId", order.getAppOrderNo());
                modelAndView.addObject("cancelUrl", payRequest.getCancelUrl());
                modelAndView.addObject("backUrl", payRequest.getBackUrl());
                modelAndView.addObject("successUrl", payRequest.getSyncUrl());
                modelAndView.addObject("subject", order.getSubjects());
                modelAndView.addObject("descp", order.getDescp());
                modelAndView.addObject("orderFeeY", UtilMoney.fenToYuan(order.getFee(), true));
                return modelAndView;

            } else {
                return new ModelAndView(UtilString.makeErrorPage(5001, reMap.getOrDefault("return_msg", "微信支付出错")));
            }
        } catch (Exception e) {
            log.error("微信APP支付订单创建出错{}", e.getMessage());
            return new ModelAndView(UtilString.makeErrorPage(5000, e.getMessage()));
        }
    }

    /**
     * SDK 支付 返回JSON
     *
     * @param payRequest
     * @param order
     * @param chargeConfig
     * @return
     */
    @Override
    public String sdkPay(PayRequest payRequest, Order order, ChargeAccount chargeConfig) {
        ChargeWechatModel chargeModel = (ChargeWechatModel) chargeConfig;
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("appid", chargeModel.getAppID());
        data.put("mch_id", chargeModel.getMerchID());
        data.put("nonce_str", UtilWechat.generateNonceStr());
        data.put("body", UtilString.getLongString("[" + order.getSubjects() + "]" + order.getDescp(), 128));
        data.put("sign_type", UtilWechat.SignType.MD5.name());
        data.put("out_trade_no", order.getId());
        data.put("total_fee", String.valueOf(order.getFee()));
        data.put("time_expire",
                DateUtils.simpleDateTimeNoSymbolFormatter().format(DateUtils.dateAdd(order.getCreatedAt(), 0, 0, 0, 0
                        , order.getExpiredTimeMinute(), 0)));

        data.put("notify_url", domain + "/payment/wechat/" + order.getId());
        if (EnumPayType.WechatSDK.getCode().equals(payRequest.getPayType())) {
            data.put("trade_type", TradeType.APP.name());
        } else if (EnumPayType.WechatNAtive.getCode().equals(payRequest.getPayType())) {
            data.put("trade_type", TradeType.NATIVE.name());
        }
        String sign = "";
        try {
            sign = UtilWechat.generateSignature(data, chargeModel.getMerchKey());
        } catch (Exception e) {
            log.error("微信加签错误", e.getMessage());
            return "";
        }
        data.put(UtilWechat.FIELD_SIGN, sign);
        try {
            String repStr = UtilHttpClient.doPostStr(WechatService.CreateOrderUrl, UtilWechat.mapToXml(data));
            Map<String, String> reMap = processResponseXml(repStr, chargeModel.getMerchKey());
            return ObjectHelper.jsonString(reMap);
        } catch (Exception e) {
            log.error("微信APP支付订单创建出错{}", e.getMessage());
            return "";
        }

    }

    /**
     * 订单支付状态查询 远程查
     *
     * @param orderId
     * @param chargeConfig
     */
    @Override
    public OrderQueryResponse orderQueryFromRemote(String orderId, ChargeAccount chargeConfig) {
        ChargeWechatModel chargeWechatModel = (ChargeWechatModel) chargeConfig;
        Map<String, String> reMap = new HashMap<>();
        String return_code;
        try {
            TreeMap<String, String> map = new TreeMap<>();
            map.put("appid", chargeWechatModel.getAppID());
            map.put("mch_id", chargeWechatModel.getMerchID());
            map.put("out_trade_no", orderId);
            map.put("nonce_str", UtilWechat.generateNonceStr());
            String sign = UtilWechat.generateSignature(map, chargeWechatModel.getMerchKey());
            map.put(UtilWechat.FIELD_SIGN, sign); //加签
            String repStr = UtilHttpClient.doPostStr(WechatService.CreateOrderUrl, UtilWechat.mapToXml(map));
            reMap = processResponseXml(repStr, chargeWechatModel.getMerchKey());


            if (reMap != null && reMap.containsKey(RETURN_CODE)) {

                if (SUCCESS.equalsIgnoreCase(reMap.get(RETURN_CODE))) {
                    if (SUCCESS.equalsIgnoreCase(reMap.get("result_code"))) {
                        OrderQueryResponse orderQueryResponse = orderQueryResponse = OrderQueryResponse.builder()
//                                    .unionOrderID(orderId)
                                .unionOrderID(reMap.get("out_trade_no"))
                                .paymentOrderID(reMap.get("transaction_id"))
                                .orderFee(Integer.parseInt(reMap.get("total_fee")))
                                .payFee(Integer.parseInt(reMap.get("cash_fee")))
                                .thirdStatus(reMap.get("trade_state"))
                                .thirdCode(reMap.get("result_code"))
                                .thirdMsg(reMap.get("err_code") + "#" + reMap.get("err_code_des")).build();
                        ;
                  /*      SUCCESS—支付成功
                        REFUND—转入退款
                        NOTPAY—未支付
                        CLOSED—已关闭
                        REVOKED—已撤销（付款码支付）
                        USERPAYING--用户支付中（付款码支付）
                        PAYERROR--支付失败(其他原因，如银行返回失败)*/

                        //支付状态成功
                        if (SUCCESS.equalsIgnoreCase(reMap.get("trade_state"))) {
                            orderQueryResponse.setStatus(EnumOrderStatus.PaySuccess.getCode());
                        } else if ("NOTPAY".equalsIgnoreCase(reMap.get("trade_state")) || "USERPAYING".equalsIgnoreCase(reMap.get("trade_state"))) {
                            orderQueryResponse.setStatus(EnumOrderStatus.Created.getCode());
                        } else if ("PAYERROR".equalsIgnoreCase(reMap.get("trade_state")) || "CLOSED".equalsIgnoreCase(reMap.get("trade_state"))) {
                            orderQueryResponse.setStatus(EnumOrderStatus.PayFailed.getCode());
                        }
                        return orderQueryResponse;
                    } else {
                        log.error("执行【{}】微信订单状态查询出错{}", orderId, JSON.toJSONString(reMap));
                        return OrderQueryResponse.builder().thirdCode(reMap.get("result_code")).thirdMsg(reMap.get(
                                "err_code") + reMap.get("err_code_des")).build();
                    }

                } else {
                    log.error("执行【{}】微信订单状态查询出错{}", orderId, JSON.toJSONString(reMap));
                    return OrderQueryResponse.builder().thirdCode(reMap.get(RETURN_CODE)).thirdMsg(reMap.get(
                            "return_msg")).build();

                }


            } else {
                log.error("执行【{}】微信订单状态查询出错{}", orderId, reMap == null ? "查询响应为空" : JSON.toJSONString(reMap));
                return OrderQueryResponse.builder().thirdCode(reMap == null ? "5000" : reMap.get(RETURN_CODE)).thirdMsg(reMap == null ? "查询响应为空" : reMap.get("return_msg")).build();

            }

        } catch (Exception e) {
            log.error("执行【{}】微信订单状态查询出错{}", orderId, e);
            return OrderQueryResponse.builder().thirdCode("5000").thirdMsg(e.getMessage()).build();
        }


    }

    /**
     * 退款状态查询 远程查
     *
     * @param orderId
     * @param chargeConfig
     */
    @Override
    public OrderRefundQueryResponse orderRefundQuery(String orderId, ChargeAccount chargeConfig) {
        return null;
    }


    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     *
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
    public Map<String, String> processResponseXml(String xmlStr, String key) throws Exception {

        String return_code;
        Map<String, String> respData = UtilWechat.xmlToMap(xmlStr);
        if (respData.containsKey(RETURN_CODE)) {
            return_code = respData.get(RETURN_CODE);
        } else {
            throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
        }

        if (return_code.equals(FAIL)) {
            return respData;
        } else if (return_code.equals(SUCCESS)) {
            if (isResponseSignatureValid(respData, key)) {
                return respData;
            } else {
                throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
            }
        } else {
            throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
        }
    }


    /**
     * 判断xml数据的sign是否有效，必须包含sign字段，否则返回false。
     *
     * @param reqData 向wxpay post的请求数据
     * @return 签名是否有效
     * @throws Exception
     */
    public boolean isResponseSignatureValid(Map<String, String> reqData, String key) throws Exception {
        // 返回数据的签名方式和请求中给定的签名方式是一致的
        return UtilWechat.isSignatureValid(reqData, key, UtilWechat.SignType.MD5);
    }

}