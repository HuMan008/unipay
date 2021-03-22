package cn.gotoil.unipay.web.controller.api.v1;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.exception.CommonError;
import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.bill.web.annotation.Authentication;
import cn.gotoil.bill.web.helper.ServletRequestHelper;
import cn.gotoil.bill.web.interceptor.authentication.AuthenticationType;
import cn.gotoil.unipay.classes.HashCompareAuthenticationKeyProvider;
import cn.gotoil.unipay.classes.PayDispatcher;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.web.helper.RedissonLockHelper;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.request.RefundRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.services.BasePayService;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import cn.gotoil.unipay.web.services.OrderService;
import cn.gotoil.unipay.web.services.RefundService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

/**
 * api入口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、15:05
 */
@RestController
@RequestMapping({"api/v1", "api/v2"})
@Authentication(authenticationType = AuthenticationType.Signature)
public class APIPayController {

    @Autowired
    OrderService orderService;
    @Autowired
    ChargeConfigService chargeConfigService;

    @Autowired
    RefundService refundService;


    @Autowired
    PayDispatcher payDispatcher;


    @RequestMapping(value = "dopay", method = RequestMethod.POST)
    @ApiOperation(value = "API订单创建", position = 5)
    public Object createOrderAction(@Valid @RequestBody PayRequest payRequest, HttpServletRequest request) {
        if (!payRequest.getAppId().equals(ServletRequestHelper.XU())) {
            throw new BillException(UnipayError.CreatOrderError);
        }
        //校验请求
        orderService.checkPayRequest(payRequest);
        //填充请求 有些参数请求里没传的
        orderService.fillPayRequest(payRequest);
        //创建订单（不持久化）
        Order order = orderService.warpPayRequest2UnionOrder(payRequest, request);
        String payInfoStr = shunt(order);
        orderService.saveOrder(order);
        return warpPayInfoStr2Object(payInfoStr,order.getExtraParam(),
                HashCompareAuthenticationKeyProvider.key(payRequest.getAppId()));
    }

private String  shunt(Order order){
    EnumPayType payType = EnumUtils.getEnum(EnumPayType.class, order.getPayType());
    ChargeConfig chargeConfig = chargeConfigService.loadByAppIdPayType(order.getAppId(), payType.getCode());
    if (!PayDispatcher.SDK_PAY_SET.contains(payType)) {
        throw new BillException(UnipayError.PayTypeNotImpl);
    }
    String payInfoStr = new String();
    BasePayService payService = payDispatcher.payServerDispatcher(payType, order.getApiVersion());
    if(payService==null){
        throw new BillException(UnipayError.PayTypeNotImpl);
    }

    ChargeAccount chargeAccount = payDispatcher.getChargeAccountBean(chargeConfig, order.getApiVersion());
    if(chargeAccount==null){
        throw new BillException(UnipayError.AppNotSupportThisPay);
    }
    payInfoStr =payService.sdkPay(order,chargeAccount);

    if (StringUtils.isEmpty(payInfoStr)) {
        throw new BillException(CommonError.SystemError);
    }
    return payInfoStr;
}
private Object warpPayInfoStr2Object(String payInfoStr,String extraParam,String appKey){
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("payData", payInfoStr);
    jsonObject.put("extraParam", extraParam);
    jsonObject.put("sign", Hash.md5(appKey + payInfoStr));
    return jsonObject;
}

    @RequestMapping(value = "repay/{appOrderNo}", method = RequestMethod.POST)
    @ApiOperation(value = "API继续支付", position = 5)
    public Object rePayOrderAction(@PathVariable String appOrderNo) {
        Order order = orderService.loadByAppOrderNo(appOrderNo,ServletRequestHelper.XU());
        Optional.ofNullable(order).orElseThrow(() -> new BillException(UnipayError.OrderNotExists));
        if(!orderService.rePayOrderCheck(order)){
            throw new BillException(UnipayError.OrdeeStatusErrorOrOrderExpirsed);
        }
            String payInfoStr= shunt(order);
        return  warpPayInfoStr2Object(payInfoStr,order.getExtraParam(),
                HashCompareAuthenticationKeyProvider.key(order.getAppId()));

    }

    @RequestMapping(value = "remoteQuery/{oid:^\\d{21}$}", method = RequestMethod.POST)
    @ApiOperation(value = "订单支付状态 远程", position = 10)
    public Object queryOrderFromRemoteAction(@PathVariable String oid, HttpServletRequest request) {
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
        if (RedissonLockHelper.isLocked(RedissonLockHelper.Key.Refund + refundRequest.getAppOrderRefundNo())) {
            throw new BillException(UnipayError.SystemBusy);
        }
        boolean r = RedissonLockHelper.tryLock(RedissonLockHelper.Key.Refund + refundRequest.getAppOrderRefundNo());
        if (!r) {
            throw new BillException(UnipayError.SystemBusy);
        }
        Order order = orderService.loadByAppOrderNo(refundRequest.getAppOrderNo(), ServletRequestHelper.XU());
        Optional.ofNullable(order).orElseThrow(() -> new BillException(UnipayError.OrderNotExists));
        if(EnumOrderStatus.PaySuccess.getCode()!=order.getStatus()){
            throw new BillException(UnipayError.RefundError_OrderStatusError);
        }
        try {
            return refundService.refund(order, refundRequest);
        }catch(Exception e){
            throw new BillException(5000,e.getMessage());
        }finally {
            RedissonLockHelper.unlock(RedissonLockHelper.Key.Refund + refundRequest.getAppOrderRefundNo());

        }
    }
   /* @RequestMapping(value = "refundQuery/{refundId:^r_\\d{21}_\\d+$}",method = RequestMethod.POST)
    @ApiOperation(value = "退款申请查询",position = 20)
    public Object refundAction(@PathVariable String refundId){
        return refundService.refundQueryFromRemote(refundId);
    }
*/
    @RequestMapping(value = "refundQuery/{refundId:^r_\\d{21}_\\d+$}",method = RequestMethod.POST)
    @ApiOperation(value = "退款状态查询",position = 20)
    public Object refundQueryAction(@PathVariable String refundId){
        return refundService.refundQuery(refundId,1);
    }


}
