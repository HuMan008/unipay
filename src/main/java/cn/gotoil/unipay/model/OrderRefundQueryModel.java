package cn.gotoil.unipay.model;

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
public class OrderRefundQueryModel {

    /**
     * 我的订单ID 提交给支付方的商户订单号
     */
    String unionOrderID;

    /**
     * 订单支付金额（除掉折扣)
     */
    int payFee;

    /**
     * 商户到账金额
     */
    int orderArrFee;

    /**
     * 订单金额
     */
    int orderFee;

    /**
     * 累计成功退款金额
     */
    int totolRefundFee;


    List<RefundDetail> refundDetail = new ArrayList<>();


}

