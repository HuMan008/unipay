package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.mapper.ext.ExtOrderQueryMapper;
import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryListRequest;
import cn.gotoil.unipay.web.message.request.admin.OrderQueryPayingListRequest;
import cn.gotoil.unipay.web.services.OrderQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderQuerySerivceImpl implements OrderQueryService {

    @Resource
    ExtOrderQueryMapper extOrderQueryMapper;

    /**
     * 查询
     * @param orderQueryListRequest
     * @return
     */
    @Override
    public BasePageResponse queryOrder(OrderQueryListRequest orderQueryListRequest){
        BasePageResponse pageResponse = new BasePageResponse();
        BeanUtils.copyProperties(orderQueryListRequest, pageResponse);
        Map<String, Object> params = orderQueryListRequest.getParams();
        int total = extOrderQueryMapper.queryOrderCounts(params);
        pageResponse.setTotal(total);
        pageResponse.setRows(extOrderQueryMapper.queryOrder(params));
        return pageResponse;
    }

    /**
     * 查询支付中的订单
     * @param orderQueryPayingListRequest
     * @return
     */
    @Override
    public BasePageResponse queryPayingOrder(OrderQueryPayingListRequest orderQueryPayingListRequest){
        BasePageResponse pageResponse = new BasePageResponse();
        BeanUtils.copyProperties(orderQueryPayingListRequest, pageResponse);
        Map<String, Object> params = orderQueryPayingListRequest.getParams();
        params.put("status", EnumOrderStatus.Created);
        int total = extOrderQueryMapper.queryOrderCounts(params);
        pageResponse.setTotal(total);
        pageResponse.setRows(extOrderQueryMapper.queryOrder(params));
        return pageResponse;
    }

    /**
     * 支付中的订单 特定
     *
     * @return
     */
    @Override
    public List<Order> queryOrderByIn10() {
        return extOrderQueryMapper.queryOrderByIn10();
    }

    /**
     * 过期订单
     *
     * @return
     */
    @Override
    public List<Order> queryOrderByOut10() {
        return extOrderQueryMapper.queryOrderByOut10();
    }
}
