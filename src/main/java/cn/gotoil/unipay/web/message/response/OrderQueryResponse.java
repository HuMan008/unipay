package cn.gotoil.unipay.web.message.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单状态查询响应
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、9:46
 */
@Data
@Builder
@NoArgsConstructor
public class OrderQueryResponse {
    /**
     * 我的订单ID 提交给支付方的商户订单号
     */
    String unionOrderID;

    /**
     * 支付方订单号
     */
    String paymentOrderID;

    /**
     * 状态
     */
    @Builder.Default
    byte status = -127;
    String thirdStatus;

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

    String thirdCode;
    String thirdMsg;



}
