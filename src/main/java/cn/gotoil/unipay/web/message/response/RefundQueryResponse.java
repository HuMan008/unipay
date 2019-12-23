package cn.gotoil.unipay.web.message.response;

import lombok.Builder;
import lombok.Data;

/**
 * 退款查询响应
 *
 * @author think <syj247@qq.com>、
 * @date 2019-12-19、16:11
 */
@Data
@Builder
public class RefundQueryResponse {

    /**
     * 退款申请号
     */
    String orderRefundId;

    /**
     * 统一订单号
     */
    String orderId;
    /**
     * 应用订单号
     */
    String appOrderNo;
    /**
     * 应用退款订单号
     */
    String appOrderRefundNo;
    /**
     * 退款申请金额
     */
    int applyFee;
    /**
     * 退款通过金额
     */
    int passFee;
    /**
     * 第三方响应描述
     */
    String thirdMsg;
    /**
     * 第三方响应代码
     */
    String thirdCode;
    /**
     * 统一退款状态
     */
    byte refundStatus;
}
