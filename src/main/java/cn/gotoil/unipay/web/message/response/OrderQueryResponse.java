package cn.gotoil.unipay.web.message.response;

import cn.gotoil.unipay.model.entity.Order;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

/**
 * 订单状态查询响应
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、9:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderQueryResponse {
    /**
     * 我的订单ID 提交给支付方的商户订单号
     */
    String unionOrderID;

    /**
     * 应用订单号
     */
    String appOrderNO;

    /**
     * 支付方订单号
     */
    String paymentId;

    /**
     * 支付方用户ID
     */
    String paymentUid;

    /**
     * 订单支付时间  支付成功才有的
     */
    long payDateTime;

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

    public static OrderQueryResponse warpOrderToOrderQuyerResponse(Order order) {
        return OrderQueryResponse.builder()
                .unionOrderID(order.getId())
                .appOrderNO(order.getAppOrderNo())
                .paymentId(StringUtils.isEmpty(order.getPaymentId()) ? "" : order.getPaymentId())
                .payDateTime(order.getOrderPayDatetime() == null ? 0 : order.getOrderPayDatetime())
                .status(order.getStatus())
                .orderFee(order.getFee() == null ? 0 : order.getFee())
                .payFee(order.getPayFee() == null ? 0 : order.getPayFee())
                .thirdStatus("-")
                .thirdCode("-")
                .thirdMsg("本地查询").build();
    }



}
