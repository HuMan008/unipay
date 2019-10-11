package cn.gotoil.unipay.web.controller.api.v1;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.exception.CommonError;
import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.bill.web.helper.ServletRequestHelper;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.classes.HashCompareAuthenticationKeyProvider;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.services.AlipayService;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import cn.gotoil.unipay.web.services.OrderService;
import cn.gotoil.unipay.web.services.WechatService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * api入口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、15:05
 */
@RestController
@RequestMapping("api/v1")
public class APIController {

    @Autowired
    OrderService orderService;
    @Autowired
    ChargeConfigService chargeConfigService;

    @Autowired
    AlipayService alipayService;

    @Autowired
    WechatService wechatService;

    @RequestMapping(value = "dopay", method = RequestMethod.POST)
    @ApiOperation(value = "API订单创建", position = 5)
    public BillApiResponse createOrder(@Valid @RequestBody PayRequest payRequest) {
        if (!payRequest.getAppId().equals(ServletRequestHelper.XU())) {
            throw new BillException(UnipayError.CreatOrderError);
        }
        //校验请求
        orderService.checkPayRequest(payRequest);
        //填充请求 有些参数请求里没传的
        orderService.fillPayRequest(payRequest);
        //创建订单（不持久化）
        Order order = orderService.warpPayRequest2UnionOrder(payRequest);

        EnumPayType payType = EnumUtils.getEnum(EnumPayType.class, payRequest.getPayType());
        ChargeConfig chargeConfig = chargeConfigService.loadByAppIdPayType(payRequest.getAppId(), payType.getCode());
        String payInfoStr = new String();
        switch (payType) {
            case WechatSDK: {
                ChargeWechatModel chargeWechatModel =
                        JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                ChargeWechatModel.class);
                payInfoStr = wechatService.sdkPay(payRequest, order, chargeWechatModel);
                break;
            }
            case WechatNAtive: {
                ChargeWechatModel chargeWechatModel =
                        JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                ChargeWechatModel.class);
                payInfoStr = wechatService.sdkPay(payRequest, order, chargeWechatModel);
                break;
            }
            case AlipaySDK:
                ChargeAlipayModel chargeAlipayModel =
                        JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                ChargeAlipayModel.class);
                payInfoStr = alipayService.sdkPay(payRequest, order, chargeAlipayModel);
                break;
            default:
                throw new BillException(UnipayError.PayTypeNotImpl);
        }
        if (StringUtils.isEmpty(payInfoStr)) {
            throw new BillException(CommonError.SystemError);
        }
        orderService.saveOrder(order);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("payData", payInfoStr);
        jsonObject.put("extraParam", order.getExtraParam());
        jsonObject.put("sign", Hash.md5(HashCompareAuthenticationKeyProvider.key(payRequest.getAppId()) + payInfoStr));
        return new BillApiResponse(jsonObject);
    }


    @RequestMapping(value = "query/{oid}", method = RequestMethod.POST)
    @ApiOperation(value = "订单支付状态 远程", position = 10)
    public BillApiResponse queryOrderFromRemote(@PathVariable String oid) {
        Order o = orderService.loadByOrderID(oid);
        if (o == null) {
            throw new BillException(UnipayError.OrderNotExists);
        }
        OrderQueryResponse orderQueryResponse = orderService.queryOrderStatusFromRemote(o);
        if (orderQueryResponse == null || orderQueryResponse.getStatus() == -127) {
            throw new BillException(UnipayError.PayTypeNotImpl);
        }
        return new BillApiResponse(orderQueryResponse);
    }
}
