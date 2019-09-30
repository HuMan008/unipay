package cn.gotoil.unipay.web.controller.notify;

import cn.gotoil.bill.tools.date.DateUtils;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.utils.UtilWechat;
import cn.gotoil.unipay.web.helper.RedisLockHelper;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import cn.gotoil.unipay.web.services.OrderService;
import cn.gotoil.unipay.web.services.WechatService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.CharStreams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信通知入口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-27、11:48
 */
@Slf4j
@RequestMapping("payment/wechat")
@Controller
public class WechatNotifyController {

    @Autowired
    RedisLockHelper redisLockHelper;
    @Autowired
    OrderService orderService;
    @Autowired
    ChargeConfigService chargeConfigService;

    @RequestMapping(value = {"{orderId:^\\d{21}$}"})
    @NeedLogin(value = false)
    public String asyncNotify(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                              @PathVariable String orderId) throws Exception {
        log.info("微信异步通知");

        String requestBodyXml = "";//响应xml
        Map<String, String> mm = new HashMap<>();
        try {
            //判断是否正在处理这个订单
            if (redisLockHelper.hasLock(orderId)) {
                mm.put(WechatService.RETURN_CODE, "FAIL");
                mm.put("return_msg", "订单号错误");
                return UtilWechat.mapToXml(mm);
            }
            redisLockHelper.addLock(orderId, false, 0, null);
            requestBodyXml = CharStreams.toString(httpServletRequest.getReader());
            //响应XML转成Map
            Map<String, String> reMap = UtilWechat.xmlToMap(requestBodyXml);
            if (reMap.containsKey(WechatService.RETURN_CODE) && WechatService.SUCCESS.equals(reMap.get(WechatService.RETURN_CODE).toString())) {
                //路径里的订单号不等于通知内容里的商户订单号，直接返回处理失败
                if (!orderId.equals(reMap.get("out_trade_no"))) {
                    mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                    mm.put("return_msg", "订单号错误");
                    return UtilWechat.mapToXml(mm);
                }
                //订单查询
                Order order = orderService.loadByOrderID(orderId);
                if (order == null) {
                    mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                    mm.put("return_msg", "订单不存在");
                    return UtilWechat.mapToXml(mm);
                }
                //订单不是支付中状态
                if (EnumOrderStatus.Created.getCode() != order.getStatus().byteValue()) {
                    mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                    mm.put("return_msg", "本地订单状态不正确");
                    return UtilWechat.mapToXml(mm);
                }
                //支付方式 APP NATIVATE JSAPI
                String trade_type = reMap.get("trade_type");
                //根据订单找到这个订单的收款账号
                ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
                ChargeWechatModel model = JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                        ChargeWechatModel.class);

                String merchanKey = model == null ? "" : model.getMerchKey();
                if (UtilWechat.isSignatureValid(reMap, merchanKey)) {
                    //支付成功
                    if (WechatService.SUCCESS.equalsIgnoreCase(reMap.get(WechatService.RESULT_CODE))) {
                        //                        更新订单
                        Order newOrder = new Order();
                        newOrder.setId(order.getId());
                        newOrder.setPayFee(Integer.parseInt(reMap.get("total_fee")));
                        newOrder.setStatus(EnumOrderStatus.PaySuccess.getCode());
                        if (StringUtils.isNotEmpty(reMap.get("time_end"))) {
                            //yyyyMMddHHmmss
                            newOrder.setOrderPayDatetime(DateUtils.simpleDateTimeNoSymbolFormatter().parse(reMap.get(
                                    "time_end")).getTime());
                        } else {
                            newOrder.setOrderPayDatetime(0L);
                        }
                        newOrder.setPaymentId(reMap.get("openid"));
                        newOrder.setPaymentId(reMap.get("transaction_id"));
                        //更新订单 todo
                        //发通知；
                    /*    int u = unionOrderService.update(order, newUnionOrder); //更新订单

                        if (u != 1) {
                            beenToldMsg.setDeal(EnumStatus.Disable.getCode());
                            beenToldMsg.setDealMsg("订单状态已经被更新了");
                            logger.error("微信支付异步通知处理订单的时候发现订单已经被修改了");
                            return false;
                        }
                        logger.info("微信异步通知处理订单支付结果{}-->{}", order.toString(), newUnionOrder.toString());


                        App app = appService.findById(order.getAppId());
                        UnionNotifyBean unionNotifyBean = new UnionNotifyBean(order.getAppId(), order.getId(),
                                order.getAppOrderNo(),
                                newUnionOrder.getThirdOrderNo(),
                                EnumOrderStatus.wechatSdkResultCode2UnionPayReslut(reMap.get(ConstsWechat
                                .RESULT_CODE)).getCode(),
                                order.getOrderFee(),
                                newUnionOrder.getOrderActualFee(),
                                order.getNotifyUrl(), order.getExtraParam(), newUnionOrder.getOrderPayDatetime()
                        );
                        //            2、发送消息
                        rabbitMqSender.send(unionNotifyBean, 0, app.getAppKey());
                        beenToldMsg.setDealMsg(beenToldMsg.getDealMsg() + "并发送通知");
                        logger.info("微信异步通知{}处理完成并发送通知[{}]", order.toString(), unionNotifyBean.toString());

                        return true;*/
                    }
                    mm.put(WechatService.RETURN_CODE, WechatService.SUCCESS);
                    mm.put("return_msg", "OK");
                    return UtilWechat.mapToXml(mm);

                } else {
                    mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                    mm.put("return_msg", "签名验证失败");
                    return UtilWechat.mapToXml(mm);
                }
            } else {
                mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
                mm.put("return_msg", "消息为不正常消息");
                return UtilWechat.mapToXml(mm);
            }


        } catch (Exception e) {
            log.error("微信支付异步通知处理异常{}", e);
            mm.put(WechatService.RETURN_CODE, WechatService.FAIL);
            mm.put("return_msg", e.getMessage());
            return UtilWechat.mapToXml(mm);

        } finally {
            redisLockHelper.releaseLock(orderId);
        }
    }

    @NeedLogin(value = false)
    @RequestMapping("return")
    public void syncNotify(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                           @PathVariable String orderId) {
        log.info("微信同步通知");

    }
}
