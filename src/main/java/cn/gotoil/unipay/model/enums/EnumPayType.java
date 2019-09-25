package cn.gotoil.unipay.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式
 * 请求传过来的
 */
@Getter
@AllArgsConstructor
public enum EnumPayType {

    WechatH5("WechatH5", "微信H5支付", EnumPayCategory.Wechat), WechatJSAPI("WechatJSAPI", "微信JSAPI支付",
            EnumPayCategory.Wechat), WechatSDK("WechatSDK", "微信SDK支付", EnumPayCategory.Wechat), AlipayH5("AlipayH5",
            "支付宝H5", EnumPayCategory.Alipay), AlipaySDK("AlipaySDK", "支付宝SDK", EnumPayCategory.Alipay),
    ;
    String code;
    String descp;
    EnumPayCategory enumPayCategory;
}
