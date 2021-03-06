package cn.gotoil.unipay.model;

import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  订单支付  通知内容定义
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-12、16:28
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    String paymentId;

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
    int arrFee = 0;


    /**
     * 通知地址
     */
    String asyncUrl;

    /**
     * 扩展参数
     */
    String extraParam;

    /**
     * 支付时间 10位时间撮
     */
    long payDate;


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
     * 通知类型
     */
    @Builder.Default
    byte sendType =0;


}

