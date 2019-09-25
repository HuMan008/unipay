package cn.gotoil.unipay.web.message.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 订单退款申请请求
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、9:41
 */
@Data
public class OrderRefundRequest {
    @NotNull String appOrderNo;
    @NotNull @Min(1) int applyFee;
    String appOrderRefundNo;
}
