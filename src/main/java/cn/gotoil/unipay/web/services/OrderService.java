package cn.gotoil.unipay.web.services;


import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 订单服务
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、15:11
 */
public interface OrderService {

    String APPORDERNOKEY = "Unipay:AppOrderNo:";

    String APIVERSIONV1 = "v1";
    String APIVERSIONV2 = "v2";
    String APIVERSIONV_Old = "v1.0";


    /**
     * 校验支付请求参数
     *
     * @param payRequest
     */
    void checkPayRequest(PayRequest payRequest);

    /**
     * 检查继续支付的订单是否可以继续支付
     * @param order
     * @return true 可以继续支付 ;false 不可以
     */
    boolean rePayOrderCheck(Order order);

    /**
     * 补充请求里每天的参数
     *
     * @param payRequest
     */
    void fillPayRequest(PayRequest payRequest);

    /**
     * 应用订单号是否存在
     *
     * @param appOrderNo
     * @return true 合格 false 不合格
     */
    boolean checkOrderNo(String appOrderNo, String appId);

    /**
     * 根据应用订单号找对象
     *
     * @param appOrderNo
     * @param appId
     * @return
     */
    Order loadByAppOrderNo(String appOrderNo, String appId);


    /**
     * payRequest 转换成 订单
     *
     * @param payRequest 支付请求
     */
    Order warpPayRequest2UnionOrder(PayRequest payRequest, HttpServletRequest request);

    /**
     * 根据主键 统一订单号找订单
     *
     * @param orderID
     * @return
     */
    Order loadByOrderID(String orderID);


    /**
     * 更新订单
     */
    int updateOrder(Order dbOrder, Order newOrder);

    /**
     * 保存订单
     */
    int saveOrder(Order order);

    /**
     * 远程查询订单状态
     *
     * @param order
     * @return
     */
    OrderQueryResponse queryOrderStatusFromRemote(Order order);


    /**
     * 订单状态同步
     *
     * @param order
     */
    void syncOrderWithReomte(Order order);

    /**
     * 异步通知处理
     *
     * @param orderId
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    ModelAndView syncUrl(String orderId, HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse) throws Exception;

    /**
     * 单独为某个订单发送通知
     * @param order
     * @return
     */
    BillApiResponse manualSendNotify(Order order);

    /**
     * 单独为某个订单发送退款通知
     * @param refund
     * @return
     */
    BillApiResponse manualSendNotify(Refund refund);
    /**
     * 订单状态本地查询
     * @param appOrderNo
     * @param appId
     * @return
     */
    OrderQueryResponse orderQueryLocal(String appOrderNo, String appId);

    /**
     * 订单状态本地查询
     * @param order
     * @return
     */
    OrderQueryResponse orderQueryLocal(Order order);
}
