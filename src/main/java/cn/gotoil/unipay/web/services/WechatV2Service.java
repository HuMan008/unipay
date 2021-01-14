package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.ChargeWechatV2Model;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumPayType;

/**
 * 微信第三版支付接口
 *
 * @author think <syj247@qq.com>、
 * @date 2021/1/6、9:58
 */
public interface WechatV2Service extends BasePayService<ChargeWechatV2Model> {
    String QUERY_ORDER_URL = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/";
    String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    String REFUND_QUERY_URL = "https://api.mch.weixin.qq.com/pay/refundquery";

    /**
     * 获取创建订单路径
     *
     * @param t
     * @return
     */
    default String createOrderUrl(TradeType t) {
        String url = "https://api.mch.weixin.qq.com/v3/pay/transactions/";
        switch (t) {
            case APP:
                url = url + "app";
                break;
            case MWEB:
                url = url + "h5";
                break;
            case JSAPI:
                url = url + "jsapi";
                break;
            case Applet:
                url = url + "jsapi";
            case NATIVE:
                url = url + "native";
                break;
        }
        return url;
    }

    /**
     * 获取创建订单路径
     *
     * @param order
     * @return
     */
    default String createOrderUrl(Order order) {
        EnumPayType enumPayType = EnumPayType.valueOf(order.getPayType());
        switch (enumPayType) {
            case WechatH5:
                return createOrderUrl(TradeType.MWEB);
            case WechatSDK:
                return createOrderUrl((TradeType.APP));
            case WechatJSAPI:
                return createOrderUrl(TradeType.JSAPI);
            case WechatNAtive:
                return createOrderUrl(TradeType.NATIVE);
            case WechatApplet:
                return createOrderUrl(TradeType.Applet);
            default:
                return "";
        }

    }

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
        APP,
        /**
         * 小程序
         */
        Applet,

        ;
    }


}
