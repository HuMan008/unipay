package cn.gotoil.unipay.web.controller;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.bill.web.helper.ServletRequestHelper;
import cn.gotoil.unipay.config.consts.ConstsRabbitMQ;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.utils.UtilBase64;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import cn.gotoil.unipay.web.services.OrderService;
import cn.gotoil.unipay.web.services.WechatService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import org.apache.commons.lang3.EnumUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 测试
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-12、17:07
 */
@RestController
@RequestMapping("g1")
public class TestController {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WechatService wechatService;
    @Autowired
    OrderService orderService;
    @Autowired
    ChargeConfigService chargeConfigService;

    @Value("${domain}")
    String domain;

    @RequestMapping("t2")
    public Object t3() {
        rabbitTemplate.convertAndSend(ConstsRabbitMQ.orderFirstExchangeName, ConstsRabbitMQ.orderRoutingKey,
                "我是一条消息哈哈哈哈哈");
        return "ssss";
    }


    @RequestMapping("tt1")
    public void wechatJSAPIPay(@Valid PayRequest payRequest, HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse) throws Exception {
        if (!payRequest.getAppId().equals(ServletRequestHelper.XU())) {
            throw new BillException(UnipayError.CreatOrderError);
        }
        //校验请求
        orderService.checkPayRequest(payRequest);
        //填充请求 有些参数请求里没传的
        orderService.fillPayRequest(payRequest);
        //创建订单（不持久化）
        //        Order order = orderService.warpPayRequest2UnionOrder(payRequest);

        EnumPayType payType = EnumUtils.getEnum(EnumPayType.class, payRequest.getPayType());
        ChargeConfig chargeConfig = chargeConfigService.loadByAppIdPayType(payRequest.getAppId(), payType.getCode());
        ChargeWechatModel chargeWechatModel = JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson())
                , ChargeWechatModel.class);
        String redirectURL = ObjectHelper.jsonString(payRequest);
        String param = UtilBase64.encode(redirectURL.getBytes());
        String redirectUrlP =
                "http://thirdparty.guotongshiyou.cn/third_party/oauth/wechat/default?redirect=" + domain + "/g1/tt2" + "?param=" + param;
        httpServletResponse.sendRedirect(redirectUrlP);
    }


    @RequestMapping("tt2")
    public Object t3(HttpRequest request, HttpResponse response, @RequestParam String param,
                     @RequestParam String open_id, HttpServletRequest httpServletRequest,
                     HttpServletResponse httpServletResponse) throws Exception {
        param = new String(UtilBase64.decode(param));

        try {
            param = URLDecoder.decode(param, Charsets.UTF_8.name());
            PayRequest payRequest = JSONObject.toJavaObject(JSONObject.parseObject(param), PayRequest.class);
            //校验请求
            orderService.checkPayRequest(payRequest);
            //填充请求 有些参数请求里没传的
            orderService.fillPayRequest(payRequest);
            //创建订单（不持久化）
            Order order = orderService.warpPayRequest2UnionOrder(payRequest);

            EnumPayType payType = EnumUtils.getEnum(EnumPayType.class, payRequest.getPayType());
            ChargeConfig chargeConfig = chargeConfigService.loadByAppIdPayType(payRequest.getAppId(),
                    payType.getCode());

            ChargeWechatModel chargeWechatModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeWechatModel.class);

            return wechatService.pagePay(payRequest, order, chargeWechatModel, httpServletRequest, httpServletResponse);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
