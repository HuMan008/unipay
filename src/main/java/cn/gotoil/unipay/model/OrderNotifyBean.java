package cn.gotoil.unipay.model;

import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单通知内容定义
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-12、16:28
 */
@Builder
@Data
public class OrderNotifyBean implements Serializable {

    /**
     * 应用号
     */
    String appId;

    /**
     * 我的订单ID 提交给支付方的商户订单号
     */
    String unionOrderID;
    /**
     * 通知类型 PAY 支付通知    REFUND 退款通知
     */
    @Builder.Default
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
    @Builder.Default
    byte status = -127;

    /**
     * 订单金额
     */
    @Builder.Default
    int orderFee = 0;
    /**
     * 支付金额
     */
    @Builder.Default
    int payFee = 0;
    @Builder.Default
    int refundFee = 0;
    @Builder.Default
    int totalRefundFee = 0;
    String asyncUrl;
    String extraParam;
    long payDate;
    @Builder.Default

    String sign = "";
    @Builder.Default

    long timeStamp = 0;


}
