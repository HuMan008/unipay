package cn.gotoil.unipay.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付类型，一个类型对应一种账号
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、15:20
 */
@Getter
@AllArgsConstructor
public enum EnumPayCategory {

    Wechat((byte) 2, "Wechat", "微信支付"), Alipay((byte) 3, "Alipay", "支付宝支付");
    byte codeValue;
    String code;
    String descp;
}
