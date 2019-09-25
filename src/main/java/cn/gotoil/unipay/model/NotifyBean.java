package cn.gotoil.unipay.model;

import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 通知内容定义
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-12、16:28
 */
@Builder
//@AllArgsConstructor
//@NoArgsConstructor
@Getter
@Setter
public class NotifyBean implements Serializable {
    /**
     * 我的订单ID 提交给支付方的商户订单号
     */
    String unionOrderID;
    /**
     * 通知类型 PAY 支付通知    REFUND 退款通知
     */
    String method = EnumOrderMessageType.PAY.name();

    /**
     * 传过来的订单号
     */
    String appOrderNO;
    /**
     * 支付方订单号
     */
    String paymentOrderID;

    /**
     * 状态
     */
    byte status = -127;

    /**
     * 订单金额
     */
    int orderFee = 0;
    /**
     * 支付金额
     */
    int payFee = 0;

    int refundFee = 0;
    int totalRefundFee = 0;
    String asyncUrl;
    String extraParam;
    long payDate;
    String sign = "";

    int notifyCount;
}
