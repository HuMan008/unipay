package cn.gotoil.unipay.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 */
@Getter
@AllArgsConstructor
public enum EnumOrderStatus {
    Created((byte) 1, "待支付"), PaySuccess((byte) 2, "失败"), PayFailed((byte) 0, "成功"),
    ;
    byte code;
    String descp;
}
