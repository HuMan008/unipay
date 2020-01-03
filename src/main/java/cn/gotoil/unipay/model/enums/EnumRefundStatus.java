package cn.gotoil.unipay.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 退款状态
 * @author  Suyajiang
 * @date  2019年12月18日17:02:48
 */
@Getter
@AllArgsConstructor
public enum EnumRefundStatus {
    Success((byte)0,"成功"),
    Refunding((byte)1,"申请中"),
    Failed((byte)2,"失败"),
    WaitSure((byte)3,"待确定"),


    ;

//    0退款成功 1申请中  2退款失败

    byte code;
    String descp;
}
