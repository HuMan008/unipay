package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.OrderExample;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.mapper.OrderMapper;
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

    @Resource
    OrderMapper orderMapper;

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

        params.put("offset", pageResponse.getOffset());
        params.put("pageSize", pageResponse.getPageSize());
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
     * 根据APPKEY,ORDERID获取订单
     * @param appkey
     * @param orderId
     * @return
     */
    @Override
    public Order getOrderByAppKeyAndId(String appkey,String orderId){
        OrderExample example = new OrderExample();
        example.createCriteria().andAppIdEqualTo(appkey).andIdEqualTo(orderId);
        java.util.List<Order> list = orderMapper.selectByExample(example);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    /**
     * 支付中，10分钟内的订单
     * @return
     */
    @Override
    public List<Order> queryOrderByIn10(){
        return extOrderQueryMapper.queryOrderByIn10();
    }

    /**
     * 支付中，10分钟后的订单
     */
    @Override
    public List<Order> queryOrderByOut10(){
        return extOrderQueryMapper.queryOrderByOut10();
    }
}
