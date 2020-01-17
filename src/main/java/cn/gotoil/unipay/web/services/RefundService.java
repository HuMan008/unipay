package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.OrderRefundQueryModel;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.web.message.request.RefundRequest;
import cn.gotoil.unipay.web.message.request.admin.RefundQueryListRequest;
import cn.gotoil.unipay.web.message.response.OrderRefundResponse;
import cn.gotoil.unipay.web.message.response.RefundQueryResponse;

import java.util.List;

/**
 * 退款服务接口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-12-18、16:40
 */
public interface RefundService {


    /**
     * 发起退款申请
     * @param order
     * @param refundRequest
     * @return
     */
    Object refund(Order order, RefundRequest refundRequest);

    /**
     * 查订单的所有退款记录
     *
     * @param orderId 统一订单号
     * @return
     */
    List<Refund> getRefundList(String orderId);

    /**
     * 订单查询
     * @param refundQueryId
     * @param type 0本地查询 1远程查
     * @return
     * 远程查询服务器自身调用
     */
    Object refundQuery(String refundQueryId,int type);


    /**
     * 查询订单的退款记录
     *
     * @param orderId 统一订单ID
     */
    OrderRefundQueryModel orderRefundQuery(String orderId);

    /**
     * 退款状态查询
     * @param refundId
     * @return
     */
    RefundQueryResponse refundQueryFromRemote(String refundId);

    /**
     * 退款列表
     * @param request
     * @return
     */
    Object queryRefundList(RefundQueryListRequest request);

    /**
     * 获取退款提交成功但是结果未知的 退款列表
     * @return
     */
    List<Refund> getWaitSureResultList();

    /**
     * 更新退款记录
     */
    int updateRefund(Refund dbRefund, Refund newRefund);

    /**
     * 根据退款订单id找退款对象
     */
    Refund loadById(String refundId);
}
