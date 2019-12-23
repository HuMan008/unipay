package cn.gotoil.unipay.web.controller.api.v1;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.exception.CommonError;
import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.bill.web.annotation.Authentication;
import cn.gotoil.bill.web.helper.ServletRequestHelper;
import cn.gotoil.bill.web.interceptor.authentication.AuthenticationType;
import cn.gotoil.unipay.classes.HashCompareAuthenticationKeyProvider;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.enums.EnumStatus;
import cn.gotoil.unipay.web.helper.RedisLockHelper;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.request.RefundRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * api入口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、15:05
 */
@RestController
@RequestMapping("api/v1")
@Authentication(authenticationType = AuthenticationType.Signature)
public class APIPayController {

    @Autowired
    OrderService orderService;
    @Autowired
    ChargeConfigService chargeConfigService;

    @Autowired
    AlipayService alipayService;

    @Autowired
    WechatService wechatService;

    @Autowired
    RefundService refundService;
    @Autowired
    RedisLockHelper redisLockHelper;

    @RequestMapping(value = "dopay", method = RequestMethod.POST)
    @ApiOperation(value = "API订单创建", position = 5)
    public Object createOrderAction(@Valid @RequestBody PayRequest payRequest) {
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
        return jsonObject;
    }


    @RequestMapping(value = "remoteQuery/{oid:^\\d{21}$}", method = RequestMethod.POST)
    @ApiOperation(value = "订单支付状态 远程", position = 10)
    public Object queryOrderFromRemoteAction(@PathVariable String oid) {
        Order o = orderService.loadByOrderID(oid);

        Optional.ofNullable(o).orElseThrow(() -> new BillException(UnipayError.OrderNotExists));
        Optional.of(o).filter(c ->o.getAppId().equals(ServletRequestHelper.XU())).orElseThrow(() -> new BillException(UnipayError.OrderAppMatchError));

        OrderQueryResponse orderQueryResponse = orderService.queryOrderStatusFromRemote(o);
        orderQueryResponse =Optional.ofNullable(orderQueryResponse).filter(r -> r != null && r.getStatus() != -127).orElseThrow(() -> new BillException(UnipayError.PayTypeNotImpl));

        return orderQueryResponse;
    }

    @RequestMapping(value = "query/{appOrderNo}", method = RequestMethod.POST)
    @ApiOperation(value = "订单支付状态 本地", position = 10)
    public Object queryOrderAction(@PathVariable String appOrderNo) {
        return orderService.orderQueryLocal(appOrderNo,ServletRequestHelper.XU());
    }

    @RequestMapping(value = "refund",method = RequestMethod.POST)
    @ApiOperation(value = "退款申请",position = 20)
    public Object refundAction(@Valid @RequestBody RefundRequest refundRequest){
        if(StringUtils.isEmpty(refundRequest.getAppOrderRefundNo())){
            refundRequest.setAppOrderRefundNo(refundRequest.getAppOrderNo());
        }
        if(redisLockHelper.hasLock(RedisLockHelper.Key.Refund+refundRequest.getAppOrderRefundNo())){
            throw new BillException(UnipayError.SystemBusy);
        }
        redisLockHelper.addLock(RedisLockHelper.Key.Refund+refundRequest.getAppOrderRefundNo(),true,3, TimeUnit.MINUTES);
        Order order = orderService.loadByAppOrderNo(refundRequest.getAppOrderNo(), ServletRequestHelper.XU());
        Optional.ofNullable(order).orElseThrow(() -> new BillException(UnipayError.OrderNotExists));
        if(EnumOrderStatus.PaySuccess.getCode()!=order.getStatus()){
            //todo
            throw new BillException(UnipayError.CreatOrderError);
        }
        try {
            return refundService.refund(order, refundRequest);
        }catch(Exception e){
            throw new BillException(5000,e.getMessage());
        }finally {
            redisLockHelper.releaseLock(RedisLockHelper.Key.Refund+refundRequest.getAppOrderRefundNo());
        }
    }
    @RequestMapping(value = "refundQuery/{refundId:^r_\\d{21}_\\d+$}",method = RequestMethod.POST)
    @ApiOperation(value = "退款申请查询",position = 20)
    public Object refundAction(@PathVariable String refundId){
        return refundService.refundQueryFromRemote(refundId);
    }



}
