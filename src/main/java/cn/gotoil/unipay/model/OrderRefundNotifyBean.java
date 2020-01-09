package cn.gotoil.unipay.model;

import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  退款通知
 *
 * @author think <syj247@qq.com>、
 * @date 2020年1月8日17:31:24
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRefundNotifyBean implements Serializable {

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
    String method = EnumOrderMessageType.REFUND.name();

    /**
     * 传过来的订单号
     */
    String appOrderNO;

    /**
     *  应用退款订单号
     */
    String  appRefundOrderNo;

    /**
     * 统一退款订单号
     */
    String unionRefundOrderID;

    /**
     * 申请金额
     */
    int applyFee;

    /**
     * 申请时间 10位
     */
    long  applyDateTime;

    /**
     * 申请原因/备注
     */
    String remake;

    /**
     * 状态
     */
    @Builder.Default
    byte status = -127;

    /**
     * 本次退款金额
     */
    int passFee ;

    /**
     * 完成时间 10位
     */
    long finishDate;

    /**
     * 失败原因
     */
    String failReason;

    /**
     * 通知地址
     */
    String asyncUrl;


    /**
     * 签名
     */
    @Builder.Default
    String sign = "";

    /**
     * 通知时间 10位时间戳
     */
    @Builder.Default
    long timeStamp = 0;

    /**
     * 通知次数
     */
    @Builder.Default
    int doCount = 0;

    /**
     * 通知类型 0自动 1手动
     */
    @Builder.Default
    byte sendType =0;


}

