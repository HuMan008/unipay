package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryListRequest;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryPayingListRequest;

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
}
