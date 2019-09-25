package cn.gotoil.unipay.web.message.response;

import lombok.Data;

/**
 * 订单状态查询响应
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、9:46
 */
@Data
public class OrderQueryResponse {
    /**
     * 我的订单ID 提交给支付方的商户订单号
     */
    String unionOrderID;


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


}
