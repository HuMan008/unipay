package cn.gotoil.unipay.web.message.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 退款状态查询响应
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、9:46
 */
@Data
public class OrderRefundQueryResponse {

    /**
     * 我的订单ID 提交给支付方的商户订单号
     */
    String unionOrderID;


    /**
     * 传过来的订单号
     */
    String appOrderNo;


    /**
     * 传过来的退款申请订单号
     */
    String appOrderRefundNo;


    List<RefundDetail> refundDetail = new ArrayList<>();


}

@Data
class RefundDetail {
    String refundOrderId;
    String appOrderNo;
    String appOrderRefundNo;
    int applyFee;
    String descp;
    byte processResult;

}
