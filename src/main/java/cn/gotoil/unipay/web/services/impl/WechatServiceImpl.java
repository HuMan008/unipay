package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.utils.UtilHttpClient;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.utils.UtilWechat;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.message.response.OrderRefundQueryResponse;
import cn.gotoil.unipay.web.services.WechatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信支付实现类
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-26、15:58
 */
@Service
@Slf4j
public class WechatServiceImpl implements WechatService {
    /**
     * 页面支付
     *
     * @param payRequest
     * @param order
     * @param chargeConfig
     * @return
     */
    @Override
    public ModelAndView pagePay(PayRequest payRequest, Order order, ChargeAccount chargeConfig) {
        return null;
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

        //        JSONObject jo = new JSONObject();
        //        jo.put(SysConfig.ChargeAccountIdKey, unionOrder.getChargeAccountId());
        //        jo.put("orderFee", unionOrder.getOrderFee());
        //        data.put("attach", jo.toString());
        //
        //        data.put("total_fee", unionOrder.getOrderFee() + "");
        //
        //        data.put("spbill_create_ip", UtilHttp.getIpAddr(ServletRequestHelper.httpServletRequest()));
        //        data.put("notify_url", ConstsWechat.sdkNotifyURL);
        data.put("trade_type", "APP");
        String sign = "";
        try {
            sign = UtilWechat.generateSignature(data, chargeModel.getMerchKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        data.put("sign", sign);
        try {
            String repStr = UtilHttpClient.doPostStr(WechatService.CreateOrderUrl, UtilWechat.mapToXml(data));
            Map<String, String> reMap = processResponseXml(repStr, chargeModel.getMerchKey());
            return ObjectHelper.jsonString(reMap);
        } catch (Exception e) {
            log.error("微信APP支付订单创建出错{}", e.getMessage());
        }

        return null;
    }

    /**
     * 订单支付状态查询 远程查
     *
     * @param orderId
     * @param chargeConfig
     */
    @Override
    public OrderQueryResponse orderQueryFromRemote(String orderId, ChargeAccount chargeConfig) {
        return null;
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
