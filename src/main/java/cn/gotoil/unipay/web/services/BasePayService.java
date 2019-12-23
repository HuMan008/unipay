package cn.gotoil.unipay.web.services;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.OrderRefundQueryModel;
import cn.gotoil.unipay.model.RefundDetail;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.message.response.OrderRefundResponse;
import cn.gotoil.unipay.web.message.response.RefundQueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    OrderService orderService = null;

    @Autowired
    RefundService refundService = null;

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
     * 退款申请
     *
     * @param chargeConfig
     * @param refund
     * @return
     */
    OrderRefundResponse orderRefund(T chargeConfig, Refund refund);


    /**
     * 退款状态查询
     * @param chargeConfig
     * @param refund
     * @return
     */
    RefundQueryResponse orderRefundQuery(T chargeConfig,Refund refund);


}
