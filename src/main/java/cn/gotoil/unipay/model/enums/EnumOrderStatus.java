package cn.gotoil.unipay.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 */
@Getter
@AllArgsConstructor
public enum EnumOrderStatus {
    /**
     * 待支付 订单创建默认状态
     */
    Created((byte) 1, "待支付"),
    /**
     * 支付失败 订单过期触发或者查询结果未为支付失败
     */
    PayFailed((byte) 2, "失败"),
    /**
     *支付成功 通知、订单主动查询触发
     */
    PaySuccess((byte) 0, "成功"),
    ;
    byte code;
    String descp;
}
