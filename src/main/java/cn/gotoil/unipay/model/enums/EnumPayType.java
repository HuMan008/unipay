package cn.gotoil.unipay.model.enums;

import com.google.common.base.Joiner;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 支付方式
 * 请求传过来的
 */
@Getter
@AllArgsConstructor
@SuppressWarnings("all")
public enum EnumPayType {

    WechatH5("WechatH5", "微信H5", EnumPayCategory.Wechat),

    WechatJSAPI("WechatJSAPI", "微信JSAPI", EnumPayCategory.Wechat),

    WechatSDK("WechatSDK", "微信SDK", EnumPayCategory.Wechat),

    WechatNAtive("WechatNative", "微信Native", EnumPayCategory.Wechat),

    WechatApplet("WechatApplet", "微信小程序", EnumPayCategory.Wechat),


    AlipayH5("AlipayH5", "支付宝H5", EnumPayCategory.Alipay),

    AlipaySDK("AlipaySDK", "支付宝SDK", EnumPayCategory.Alipay),
    ;
    String code;
    String descp;
    EnumPayCategory enumPayCategory;


    public static String str() {
        ;
        return Joiner.on(",").skipNulls().join(Arrays.stream(EnumPayType.values()).map(e -> e.getCode()
        ).collect(Collectors.toList()));

    }

//    public static void main(String[] args) {
//        System.out.println(str());
//    }
}
