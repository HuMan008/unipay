package cn.gotoil.unipay.web.message.request;

import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 订单查询请求
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、17:58
 */
@Data
public class OrderQueryRequest {

    /**
     * PAY 支付状态
     * REFUND 退款
     */
    @NotNull String method = EnumOrderMessageType.PAY.name();

    @NotNull String appOrderNo;

    /**
     * 当method为退款的时候 这个参数有效
     */
    //退款申请订单号
    String appOrderRefundNo;
}
