package cn.gotoil.unipay.web.services;


import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.web.message.request.PayRequest;

/**
 * 订单服务
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、15:11
 */
public interface OrderService {

    String AppOrderNoKey = "Unipay:AppOrderNo";

    /**
     * 校验支付请求参数
     *
     * @param payRequest
     */
    void checkPayRequest(PayRequest payRequest);

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
    Order warpPayRequest2UnionOrder(PayRequest payRequest);

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
}
