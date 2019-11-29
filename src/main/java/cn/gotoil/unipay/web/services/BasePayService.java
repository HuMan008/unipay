package cn.gotoil.unipay.web.services;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.message.response.OrderRefundQueryResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 支付服务类
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、15:28
 */
@Service
@ConditionalOnBean(OrderService.class)
public interface BasePayService<T extends ChargeAccount> {

    @Resource
    OrderService orderService =null ;

    /**
     * 页面支付
     *
     * @param payRequest
     * @return
     */
    ModelAndView pagePay(PayRequest payRequest, Order order, T chargeConfig, HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse);

    /**
     * SDK 支付 返回JSON
     *
     * @param payRequest
     * @return
     */
    String sdkPay(PayRequest payRequest, Order order, T chargeConfig);

    /**
     * 订单支付状态查询 远程查
     *
     * @param order
     */
    OrderQueryResponse orderQueryFromRemote(Order order, T chargeConfig);


    /**
     * 退款状态查询 远程查
     *
     * @param orderId
     */
    OrderRefundQueryResponse orderRefundQuery(String orderId, T chargeConfig);
    //    OrderNotifyBean orderQuery(String appOrderNo, String appId);
    default OrderQueryResponse orderQuery(String appOrderNo, String appId) {
        Order order = orderService.loadByAppOrderNo(appOrderNo, appId);
        Optional.ofNullable(order).map(app1 -> app1).orElseThrow(() -> new BillException(UnipayError.OrderNotExists));
        return OrderQueryResponse.warpOrderToOrderQuyerResponse(order);
    }
}
