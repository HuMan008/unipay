package cn.gotoil.unipay.web.services;

/**
 * 微信支付接口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-26、15:56
 */
public interface WechatService extends BasePayService {

    public static final String FAIL = "FAIL";
    public static final String SUCCESS = "SUCCESS";

    public static final String RETURN_CODE = "return_code";
    public static final String RESULT_CODE = "result_code";

    //下单请求地址
    String CreateOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

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
