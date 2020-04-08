package cn.gotoil.unipay.web.services;



import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.web.message.request.PayRequest;

/**
 * 微信支付接口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-26、15:56
 */
public interface WechatService extends BasePayService<ChargeWechatModel>   {

    String FAIL = "FAIL";
    String SUCCESS = "SUCCESS";

    String RETURN_CODE = "return_code";
    String RESULT_CODE = "result_code";

    //下单请求地址
    String CreateOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //订单状态查询
    String QueryOrderUrl = "https://api.mch.weixin.qq.com/pay/orderquery";


    /**
     * 退款地址
     */
    String RefundUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    /**
     * 退款查询地址
     */
    String RefundQueryUrl = "https://api.mch.weixin.qq.com/pay/refundquery";


    void setMustNeedOpenId(boolean mustNeedOpenId);

    void setPayRequest(PayRequest payRequest);

    enum TradeType {
        /**
         * jsapi
         */
        JSAPI,
        /**
         * H5
         */
        MWEB,
        /**
         * 扫码
         */
        NATIVE,
        /**
         * APP
         */
        APP
    }
}
