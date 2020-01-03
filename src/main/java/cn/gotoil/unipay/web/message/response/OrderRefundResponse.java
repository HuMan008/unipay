package cn.gotoil.unipay.web.message.response;

import lombok.Data;

/**
 * 退款申请响应
 *
 * @author think <syj247@qq.com>、
 * @date 2019-12-19、9:23
 */
@Data
public class OrderRefundResponse {

   /**
     * 退款结果查询ID
     */
    String reslutQueryId;

    /**
     * 退款状态
     *
     */
    byte refundStatus ;

    String msg;

}
