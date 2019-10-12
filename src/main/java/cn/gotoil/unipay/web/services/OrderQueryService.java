package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryListRequest;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryPayingListRequest;

import java.util.List;

public interface OrderQueryService {
    /**
     * 查询
     * @param orderQueryListRequest
     * @return
     */
    BasePageResponse queryOrder(OrderQueryListRequest orderQueryListRequest);

    /**
     * 查询支付中的订单
     * @param orderQueryPayingListRequest
     * @return
     */
    BasePageResponse queryPayingOrder(OrderQueryPayingListRequest orderQueryPayingListRequest);

    /**
     * 支付中的订单 特定
     *
     * @return
     */
    List<Order> queryOrderByIn10();

    /**
     * 过期订单
     *
     * @return
     */
    List<Order> queryOrderByOut10();
}
