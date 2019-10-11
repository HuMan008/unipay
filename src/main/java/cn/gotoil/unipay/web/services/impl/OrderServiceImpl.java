package cn.gotoil.unipay.web.services.impl;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.OrderExample;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.enums.EnumStatus;
import cn.gotoil.unipay.model.mapper.OrderMapper;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import cn.gotoil.unipay.web.services.OrderService;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
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
public class OrderServiceImpl implements OrderService {

    @Autowired
    AppService appService;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    OrderMapper orderMapper;

    @Autowired
    ChargeConfigService chargeConfigService;

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
        boolean bb = setOperations.isMember(AppOrderNoKey, appOrderNo);
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        String currentDateTime = DateUtils.simpleDateTimeWithMilliSecondNoSymbolFormatter().format(new Date());
        order.setAppId(payRequest.getAppId());
        order.setAppOrderNo(payRequest.getAppOrderNo());
        order.setAppUserId(payRequest.getAppUserId());
        order.setExpiredTimeMinute(payRequest.getExpireOutTime());
        order.setFee(payRequest.getFee());
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
        order.setCreatedAt(calendar.getTime());
        order.setUpdatedAt(order.getCreatedAt());

        //appOrderNo存一下 防止重复请求
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        setOperations.add(AppOrderNoKey, payRequest.getAppOrderNo());
        setOperations.getOperations().expire(AppOrderNoKey, 5, TimeUnit.MINUTES);
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
}
