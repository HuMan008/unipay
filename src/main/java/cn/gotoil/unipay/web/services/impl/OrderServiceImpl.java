package cn.gotoil.unipay.web.services.impl;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.OrderNotifyBean;
import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.OrderExample;
import cn.gotoil.unipay.model.enums.EnumOrderMessageType;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.enums.EnumStatus;
import cn.gotoil.unipay.model.mapper.OrderMapper;
import cn.gotoil.unipay.utils.UtilBase64;
import cn.gotoil.unipay.utils.UtilMoney;
import cn.gotoil.unipay.utils.UtilMySign;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 订单服务实现
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、15:11
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    AppService appService;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    OrderMapper orderMapper;

    @Autowired
    AlipayService alipayService;
    @Autowired
    WechatService wechatService;

    @Autowired
    ChargeConfigService chargeConfigService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 校验支付请求参数
     *
     * @param payRequest
     * @return true pass
     */
    @Override
    public void checkPayRequest(PayRequest payRequest) {
        // 检查app
        App app = appService.load(payRequest.getAppId());
        Optional.ofNullable(app).map(app1 -> app1).orElseThrow(() -> new BillException(UnipayError.AppNotExists));
        Optional.of(app).filter(c -> EnumStatus.Enable.getCode() == c.getStatus()).orElseThrow(() -> new BillException(UnipayError.AppStatusError));
        //检查应用订单号
        if (!checkOrderNo(payRequest.getAppOrderNo(), payRequest.getAppId())) {
            throw new BillException(UnipayError.AppOrderNoHasExists);
        }
        //检查收款账号
        ChargeConfig chargeConfig = chargeConfigService.loadByAppIdPayType(payRequest.getAppId(),
                payRequest.getPayType());
        Optional.ofNullable(chargeConfig).map(app1 -> app1).orElseThrow(() -> new BillException(UnipayError.AppNotSupportThisPay));
        Optional.of(chargeConfig).filter(c -> EnumStatus.Enable.getCode() == c.getStatus()).orElseThrow(() -> new BillException(UnipayError.ChargeConfigIsDisabled));

    }

    /**
     * 补充请求里每天的参数
     *
     * @param payRequest
     */
    @Override
    public void fillPayRequest(PayRequest payRequest) {
        App app = appService.load(payRequest.getAppId());
        assert app != null;
        if (StringUtils.isEmpty(payRequest.getSubject())) {
            payRequest.setSubject(app.getOrderHeader());
        }
        if (StringUtils.isEmpty(payRequest.getReMark())) {
            payRequest.setReMark(app.getOrderDescp());
        }
        if (payRequest.getExpireOutTime() == 0) {
            payRequest.setExpireOutTime(app.getDefaultOrderExpiredTime());
        }
        if (StringUtils.isEmpty(payRequest.getSyncUrl())) {
            payRequest.setSyncUrl(app.getSyncUrl());
        }
        if (StringUtils.isEmpty(payRequest.getAsyncUrl())) {
            payRequest.setAsyncUrl(app.getAsyncUrl());
        }
    }


    /**
     * 应用订单号是否存在
     *
     * @param appOrderNo
     * @param appId
     * @return true 合格 false 不合格
     */
    @Override
    public boolean checkOrderNo(String appOrderNo, String appId) {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        boolean bb = setOperations.isMember(APPORDERNOKEY + appId, appOrderNo);
        //缓存里没有
        if (!bb) {
            Order order = loadByAppOrderNo(appOrderNo, appId);
            if (order == null) { //缓存里没有 数据库也灭有
                return true;
            } else { // 缓存里没有 数据库里有
                return false;
            }
        } else {
            return false;

        }
    }


    /**
     * 根据应用订单号找对象
     *
     * @param appOrderNo
     * @param appId
     * @return
     */
    @Override
    public Order loadByAppOrderNo(String appOrderNo, String appId) {
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andAppIdEqualTo(appId).andAppOrderNoEqualTo(appOrderNo);
        List<Order> orderList = orderMapper.selectByExample(orderExample);
        if (orderList.size() == 1) {
            return orderList.get(0);
        }
        return null;
    }

    /**
     * payRequest 转换成 订单
     *
     * @param payRequest 支付请求
     */
    @Override
    public Order warpPayRequest2UnionOrder(PayRequest payRequest) {
        Order order = new Order();

        String currentDateTime = DateUtils.simpleDateTimeWithMilliSecondNoSymbolFormatter().format(new Date());
        order.setAppId(payRequest.getAppId());
        order.setAppOrderNo(payRequest.getAppOrderNo());
        order.setAppUserId(payRequest.getAppUserId());
        order.setExpiredTimeMinute(payRequest.getExpireOutTime());
        order.setFee(payRequest.getFee());
        order.setPayFee(0);
        order.setArrFee(0);
        order.setSubjects(payRequest.getSubject());
        order.setDescp(payRequest.getReMark());
        order.setExtraParam(payRequest.getExtraParam());
        order.setSyncUrl(payRequest.getSyncUrl());
        order.setAsyncUrl(payRequest.getAsyncUrl());
        order.setStatus(EnumOrderStatus.Created.getCode());
        order.setPayType(payRequest.getPayType());
        order.setPayTypeCategory(EnumUtils.getEnum(EnumPayType.class, payRequest.getPayType()).getEnumPayCategory().getCodeValue());
        //订单号生成规则 yyyyMMddHHmmss+4位流水号+2位随机数
        order.setId(currentDateTime.concat(RandomStringUtils.random(4, false, true)));
        //收款账号
        ChargeConfig chargeConfig = chargeConfigService.loadByAppIdPayType(payRequest.getAppId(),
                payRequest.getPayType());
        assert chargeConfig != null;
        order.setChargeAccountId(chargeConfig.getId());
        order.setApiVersion("v1.0");
        order.setDataVersion(0);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(order.getCreatedAt());
        order.setOrderPayDatetime(0L);
        //appOrderNo存一下 防止重复请求
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        setOperations.add(APPORDERNOKEY + payRequest.getAppId(), payRequest.getAppOrderNo());
        setOperations.getOperations().expire(APPORDERNOKEY + payRequest.getAppId(), 5, TimeUnit.MINUTES);
        return order;
    }

    /**
     * 根据主键 统一订单号找订单
     *
     * @param orderID
     * @return
     */
    @Override
    public Order loadByOrderID(String orderID) {
        return orderMapper.selectByPrimaryKey(orderID);
    }


    /**
     * 更新订单
     */
    @Override
    public int updateOrder(Order dbOrder, Order newOrder) {
        //这里要重写
        assert newOrder != null && newOrder.getId() != null && newOrder.getId().equals(dbOrder.getId());
        newOrder.setUpdatedAt(new Date());
        newOrder.setDataVersion(dbOrder.getDataVersion() + 1);
        OrderExample orderExample = new OrderExample();
        orderExample.createCriteria().andDataVersionEqualTo(dbOrder.getDataVersion()).andIdEqualTo(dbOrder.getId());
        return orderMapper.updateByExampleSelective(newOrder, orderExample);
    }

    /**
     * 保存订单
     */
    @Override
    public int saveOrder(Order order) {
        return orderMapper.insert(order);
    }


    /**
     * 远程查询订单状态
     *
     * @param order
     * @return
     */
    @Override
    public OrderQueryResponse queryOrderStatusFromRemote(Order order) {
        assert order != null;
        ChargeConfig chargeConfig = chargeConfigService.loadByAppIdPayType(order.getAppId(), order.getPayType());

        OrderQueryResponse orderQueryResponse = new OrderQueryResponse();
        if (EnumPayType.AlipayH5.getCode().equals(order.getPayType()) || EnumPayType.AlipaySDK.getCode().equals(order.getPayType())) {
            ChargeAlipayModel chargeAlipayModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeAlipayModel.class);
            orderQueryResponse = alipayService.orderQueryFromRemote(order, chargeAlipayModel);
        } else if (EnumPayType.WechatH5.getCode().equals(order.getPayType()) || EnumPayType.WechatJSAPI.getCode().equals(order.getPayType()) || EnumPayType.WechatNAtive.getCode().equals(order.getPayType()) || EnumPayType.WechatSDK.getCode().equals(order.getPayType())) {
            ChargeWechatModel chargeWechatModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeWechatModel.class);
            orderQueryResponse = wechatService.orderQueryFromRemote(order, chargeWechatModel);
        }
        return orderQueryResponse;
    }


    /**
     * 订单状态同步
     *
     * @param order
     */
    @Override
    public void syncOrderWithReomte(Order order) {
        OrderQueryResponse orderQueryResponse = queryOrderStatusFromRemote(order);
        if (orderQueryResponse != null && orderQueryResponse.getStatus() != -127) {
            if (EnumOrderStatus.PaySuccess.getCode() == orderQueryResponse.getStatus()) {
                //标记状态成功，并发通知
                Order newOrder = new Order();
                newOrder.setId(order.getId());
                newOrder.setPayFee(orderQueryResponse.getPayFee());
                newOrder.setArrFee(orderQueryResponse.getArrFee());
                newOrder.setStatus(EnumOrderStatus.PaySuccess.getCode());
                newOrder.setOrderPayDatetime(orderQueryResponse.getPayDateTime());
                newOrder.setPaymentUid(orderQueryResponse.getPaymentUid());
                newOrder.setPaymentId(orderQueryResponse.getPaymentId());
                int x = updateOrder(order, newOrder);

                if (x == 1) {
                    log.info("订单【{}】状态更新为支付成功并发送通知", order.getId());
                    OrderNotifyBean orderNotifyBean =
                            OrderNotifyBean.builder().unionOrderID(order.getId())
                                    .method(EnumOrderMessageType.PAY.name())
                                    .appOrderNO(order.getAppOrderNo())
                                    .status(newOrder.getStatus())
                                    .orderFee(order.getFee())
                                    .refundFee(0)
                                    .payFee(orderQueryResponse.getPayFee())
                                    .arrFee(orderQueryResponse.getArrFee())
                                    .totalRefundFee(0)
                                    .paymentId(newOrder.getPaymentId())
                                    .asyncUrl(order.getAsyncUrl())
                                    .extraParam(order.getExtraParam())
                                    .payDate(newOrder.getOrderPayDatetime()).
                                    timeStamp(Instant.now().getEpochSecond())
                                    .build();
                    String appSecret = appService.key(order.getAppId());
                    String signStr = UtilMySign.sign(orderNotifyBean, appSecret);
                    orderNotifyBean.setSign(signStr);
                    rabbitTemplate.convertAndSend(ConstsRabbitMQ.ORDERFIRSTEXCHANGENAME,
                            ConstsRabbitMQ.ORDERROUTINGKEY, JSON.toJSONString(orderNotifyBean));
                } else {
                    log.error("订单【{}】状态更新失败", order.getId(), orderQueryResponse);

                }
            } else if (EnumOrderStatus.PayFailed.getCode() == orderQueryResponse.getStatus()) {
                //标记状态失败
                Order newOrder = new Order();
                newOrder.setId(order.getId());
                newOrder.setStatus(EnumOrderStatus.PayFailed.getCode());
                int x = updateOrder(order, newOrder);
                if (x != 1) {
                    log.error("标记订单【{}】支付失败状态出错", order.getId());
                }
                log.info("标记订单【{}】支付失败{}", order, orderQueryResponse.toString());
            } else if (EnumOrderStatus.Created.getCode() == orderQueryResponse.getStatus()) {
                //远程状态还是待支付 啥都不用干
               /* if (org.apache.commons.lang3.time.DateUtils.addMinutes(order.getCreatedAt(),
                        order.getExpiredTimeMinute() + 12).before(new Date())) {
                    Order newOrder = new Order();
                    newOrder.setId(order.getId());
                    newOrder.setStatus(EnumOrderStatus.PayFailed.getCode());
                    int x = updateOrder(order, newOrder);
                    if (x != 1) {
                        log.error("标记订单【{}】过期失败状态出错", order.getId());
                    }
                    log.info("标记订单【{}】过期", order);
                }*/
            } else {
                log.error("订单【{}】状态查询未识别{}", order.getId(), orderQueryResponse);
            }
        } else {
            log.error("订单【{}】状态查询出错", order.getId());
        }


    }


    /**
     * 异步通知处理
     *
     * @param orderId
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @Override
    public ModelAndView syncUrl(String orderId, HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse) throws Exception {

        Order order = loadByOrderID(orderId);
        if (order == null) {
            return new ModelAndView(UtilString.makeErrorPage(UnipayError.OrderNotExists,""));
        } else if (StringUtils.isEmpty(order.getSyncUrl())) {
            //未设置同步地址
            ModelAndView modelAndView = new ModelAndView("payresult");
            modelAndView.addObject("status", order.getStatus());
            modelAndView.addObject("orderId", order.getId());
            modelAndView.addObject("paymentId", order.getPaymentId());
            modelAndView.addObject("subjects", order.getSubjects());
            modelAndView.addObject("feeY", UtilMoney.fenToYuan(order.getFee(), true));
            modelAndView.addObject("payFeeY", UtilMoney.fenToYuan(order.getPayFee(), true));
            return modelAndView;
        } else {
            OrderNotifyBean orderNotifyBean = OrderNotifyBean.builder()
                    .appId(order.getAppId())
                    .unionOrderID(order.getId())
                    .method(EnumOrderMessageType.PAY.name())
                    .appOrderNO(order.getAppOrderNo())
                    .paymentId(order.getPaymentId())
                    .status(order.getStatus())
                    .orderFee(order.getFee())
                    .payFee(order.getPayFee())
                    .arrFee(order.getArrFee())
                    .refundFee(0)
                    .totalRefundFee(0)
                    .asyncUrl(order.getAsyncUrl())
                    .extraParam(order.getExtraParam())
                    .payDate(order.getOrderPayDatetime())
                    .timeStamp(Instant.now().getEpochSecond())
                    .build();
            String param = UtilBase64.encode(JSONObject.toJSONString(orderNotifyBean).getBytes()).replaceAll("\\+",
                    "GT680");
            String sign = Hash.md5(param + appService.key(order.getAppId()));
            httpServletResponse.sendRedirect(order.getSyncUrl() + "?param=" + param + "&sign=" + sign);
            return null;
        }
    }


    /**
     * 单独为某个订单发送通知
     * @param order
     * @return
     */
    @Override
    public BillApiResponse manualSendNotify(Order order){
        assert order!=null ;
        if(EnumOrderStatus.PaySuccess.getCode() != order.getStatus()){
            throw new BillException(UnipayError.OrderStatusIsNotPaySuccess);
        }
        OrderNotifyBean orderNotifyBean =
                OrderNotifyBean.builder().unionOrderID(order.getId())
                        .method(EnumOrderMessageType.PAY.name())
                        .appOrderNO(order.getAppOrderNo())
                        .status(order.getStatus())
                        .orderFee(order.getFee())
                        .refundFee(0)
                        .payFee(order.getPayFee())
                        .arrFee(order.getArrFee())
                        .totalRefundFee(0)
                        .asyncUrl(order.getAsyncUrl())
                        .extraParam(order.getExtraParam())
                        .payDate(order.getOrderPayDatetime())
                        .timeStamp(Instant.now().getEpochSecond())
                        .paymentId(order.getPaymentId())
                        .sendType((byte)1)
                        .build();
        String appSecret = appService.key(order.getAppId());
        String signStr = UtilMySign.sign(orderNotifyBean, appSecret);
        orderNotifyBean.setSign(signStr);
        rabbitTemplate.convertAndSend(ConstsRabbitMQ.ORDERFIRSTEXCHANGENAME,
                ConstsRabbitMQ.ORDERROUTINGKEY, JSON.toJSONString(orderNotifyBean));
        return new BillApiResponse(0,"已加入消息队列",null);
    }
}
