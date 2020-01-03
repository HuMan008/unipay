package cn.gotoil.unipay.web.message.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 退款请求
 *
 * @author think <syj247@qq.com>、
 * @date 2019-12-18、16:29
 */
@Data
public class RefundRequest {

    /**
     * 商户订单号
     */
    @Size(max = 24, message = "应用订单号最大长度为24")
    @NotNull
    String appOrderNo;
    /**
     * 商户退款订单号
     */
    @Size(max = 30, message = "退款订单号最大长度为30")
    String appOrderRefundNo;


    /**
     * 退款申请金额 单位分
     */
    @NotNull(message = "退款申请金额不得为空！")
    @Min(value = 1, message = "退款申请金额不得少于1分！")
    int fee;

    /**
     * 备注
     */
    @Size(max = 50, message = "退款备注长度最长为50")
    String  remark;
}
