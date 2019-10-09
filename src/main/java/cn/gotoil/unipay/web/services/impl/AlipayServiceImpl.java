package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.utils.UtilMoney;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.message.response.OrderRefundQueryResponse;
import cn.gotoil.unipay.web.services.AlipayService;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.common.base.Charsets;
import com.google.common.net.UrlEscapers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付宝支付实现
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、17:48
 */
@Service
public class AlipayServiceImpl implements AlipayService {

    @Value("${domain}")
    String domain;

    /**
     * 退款状态查询 远程查
     *
     * @param orderId
     * @param chargeConfig
     */
    @Override
    public OrderRefundQueryResponse orderRefundQuery(String orderId, ChargeAccount chargeConfig) {
        return null;
    }

    /**
     * 页面支付
     *
     * @param payRequest
     * @param order
     * @param chargeConfig
     * @return
     */
    @Override
    public ModelAndView pagePay(PayRequest payRequest, Order order, ChargeAccount chargeConfig,
                                HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ChargeAlipayModel chargeModel = (ChargeAlipayModel) chargeConfig;
        AlipayClient alipayClient = new DefaultAlipayClient(GateWayURL, chargeModel.getAppID(),
                chargeModel.getPriKey(), Format, Charsets.UTF_8.name(), chargeModel.getPubKey(), SignType);
        AlipayTradeWapPayRequest alipayTradeWapPayRequest = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel alipayTradeWapPayModel = new AlipayTradeWapPayModel();
        alipayTradeWapPayModel.setOutTradeNo(order.getId());
        alipayTradeWapPayModel.setBody(UrlEscapers.urlFormParameterEscaper().escape(order.getDescp()));
        alipayTradeWapPayModel.setSubject(UrlEscapers.urlFormParameterEscaper().escape(order.getSubjects()));
        alipayTradeWapPayModel.setTimeoutExpress(order.getExpiredTimeMinute() + "m");
        alipayTradeWapPayModel.setTotalAmount(UtilMoney.fenToYuan(order.getFee(), false));
        if (StringUtils.isNotEmpty(payRequest.getCancelUrl())) {
            alipayTradeWapPayModel.setQuitUrl(payRequest.getCancelUrl());
        }
        alipayTradeWapPayModel.setProductCode("QUICK_WAP_WAY");
        alipayTradeWapPayRequest.setBizModel(alipayTradeWapPayModel);
        alipayTradeWapPayRequest.setNotifyUrl(domain + "/payment/alipay/" + order.getId());
        alipayTradeWapPayRequest.setReturnUrl(domain + "/payment/alipay/return/" + order.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/alipay/submit");
        try {
            String form = alipayClient.pageExecute(alipayTradeWapPayRequest).getBody();
            modelAndView.addObject("from", form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            modelAndView.addObject("errorCode", e.getErrCode());
            modelAndView.addObject("errorMsg", e.getErrMsg());
        }
        return modelAndView;
    }

    /**
     * SDK 支付 返回JSON
     *
     * @param payRequest
     * @param order
     * @param chargeConfig
     * @return
     */
    @Override
    public String sdkPay(PayRequest payRequest, Order order, ChargeAccount chargeConfig) {
        ChargeAlipayModel chargeModel = (ChargeAlipayModel) chargeConfig;

        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(GateWayURL, chargeModel.getAppID(),
                chargeModel.getPriKey(), Format, Charsets.UTF_8.name(), chargeModel.getPubKey(), SignType);
        //增加下单过程
        AlipayTradeCreateRequest tradeCreateRequest = new AlipayTradeCreateRequest();
        AlipayTradeCreateModel tradeCreateModel = new AlipayTradeCreateModel();
        tradeCreateModel.setOutTradeNo(order.getId());
        //单位元
        tradeCreateModel.setTotalAmount(UtilMoney.fenToYuan(order.getFee(), false));
        tradeCreateModel.setSubject(order.getSubjects());
        tradeCreateModel.setBody(order.getDescp());
        if (StringUtils.isNotEmpty(payRequest.getPaymentUserID())) {
            tradeCreateModel.setBuyerId(payRequest.getPaymentUserID());
        }
        tradeCreateModel.setTimeoutExpress(payRequest.getExpireOutTime() + "m");
        tradeCreateRequest.setBizModel(tradeCreateModel);
        tradeCreateRequest.setNotifyUrl(order.getAsyncUrl());
        tradeCreateRequest.setReturnUrl(order.getSyncUrl());
        try {
            //创建订单;
            AlipayTradeCreateResponse alipayTradeCreateResponse = alipayClient.sdkExecute(tradeCreateRequest);
            if (alipayTradeCreateResponse.isSuccess()) {

                //去支付
                AlipayTradeAppPayRequest alipayTradeAppPayRequest = new AlipayTradeAppPayRequest();
                AlipayTradeAppPayModel alipayTradeAppPayModel = new AlipayTradeAppPayModel();
                alipayTradeAppPayModel.setBody(order.getDescp());
                alipayTradeAppPayModel.setSubject(order.getSubjects());
                alipayTradeAppPayModel.setOutTradeNo(alipayTradeCreateResponse.getOutTradeNo());
                alipayTradeAppPayModel.setTimeoutExpress(payRequest.getExpireOutTime() + "m");
                alipayTradeAppPayModel.setTotalAmount(UtilMoney.fenToYuan(order.getFee(), false));
                alipayTradeAppPayRequest.setBizModel(alipayTradeAppPayModel);
                alipayTradeAppPayRequest.setNotifyUrl(domain + "/payment/alipay/" + order.getId());
                alipayTradeAppPayRequest.setReturnUrl(domain + "/payment/alipay/return/" + order.getId());
                AlipayTradeAppPayResponse alipayTradeAppPayResponse = alipayClient.sdkExecute(alipayTradeAppPayRequest);
                if (alipayTradeAppPayResponse.isSuccess()) {
                    logger.debug("支付宝创建订单【{}】成功");
                    return alipayTradeAppPayResponse.getBody();
                } else {
                    //关闭订单
                    AlipayTradeCloseRequest alipayTradeCloseRequest = new AlipayTradeCloseRequest();
                    AlipayTradeCloseModel alipayTradeCloseModel = new AlipayTradeCloseModel();
                    alipayTradeCloseModel.setOutTradeNo(order.getId());
                    alipayTradeCloseModel.setOperatorId("UnipayAPI");
                    alipayTradeCloseRequest.setBizModel(alipayTradeCloseModel);
                    AlipayTradeCloseResponse alipayTradeCloseResponse =
                            alipayClient.sdkExecute(alipayTradeCloseRequest);
                    if (alipayTradeCloseResponse.isSuccess()) {
                        logger.error("订单【{}】创建成功，去支付失败...关闭成功", order.getId());
                        return "";
                    } else {
                        logger.error("订单【{}】关闭失败{}", order.getId(), alipayTradeCloseResponse.getBody());
                        return "";
                    }
                }
            } else {
                logger.error("支付宝创建订单【{}】失败{}", order.getId(), alipayTradeCreateResponse.getBody());
                throw new BillException(alipayTradeCreateResponse.hashCode(), alipayTradeCreateResponse.getBody());
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝出错了{}【{}】", e.getErrCode(), e.getErrMsg());
            throw new BillException(e.hashCode(), e.getErrCode() + "【" + e.getErrMsg() + "】");
        }


    }

    /**
     * 订单支付状态查询 远程查
     *
     * thirdCode = 5000的时候表示查询异常
     * @param orderId
     * @param chargeConfig
     */
    @Override
    public OrderQueryResponse orderQueryFromRemote(String orderId, ChargeAccount chargeConfig) {
        ChargeAlipayModel chargeModel = (ChargeAlipayModel) chargeConfig;
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(orderId);
        alipay_request.setBizModel(model);
        AlipayClient client = new DefaultAlipayClient(GateWayURL, chargeModel.getAppID(),
                chargeModel.getPriKey(), Format, Charsets.UTF_8.name(), chargeModel.getPubKey(), SignType);
        try {
            AlipayTradeQueryResponse alipayTradeQueryResponse = client.execute(alipay_request);
            if (alipayTradeQueryResponse.isSuccess()) {
                String tradeStatus = alipayTradeQueryResponse.getTradeStatus();

                OrderQueryResponse orderQueryResponse = OrderQueryResponse.builder()
                        .unionOrderID(orderId)
                        .paymentOrderID(alipayTradeQueryResponse.getOutTradeNo())
                        .orderFee(UtilMoney.yuanToFen(alipayTradeQueryResponse.getTotalAmount()))
                        .payFee(UtilMoney.yuanToFen(alipayTradeQueryResponse.getBuyerPayAmount()))
                        .thirdStatus(alipayTradeQueryResponse.getTradeStatus())
                        .thirdCode(alipayTradeQueryResponse.getCode())
                        .thirdMsg(alipayTradeQueryResponse.getMsg()).build();
                //                交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS
// （交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
                if ("WAIT_BUYER_PAY".equalsIgnoreCase(tradeStatus)) {
                    orderQueryResponse.setStatus(EnumOrderStatus.Created.getCode());
                } else if ("TRADE_CLOSED".equalsIgnoreCase(tradeStatus)) {
                    orderQueryResponse.setStatus(EnumOrderStatus.PayFailed.getCode());
                } else if ("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
                    orderQueryResponse.setStatus(EnumOrderStatus.PaySuccess.getCode());
                }
                return orderQueryResponse;

            } else {
                logger.error("执行【{}】支付宝订单状态查询失败{}", orderId, JSONObject.toJSONString(alipayTradeQueryResponse));
                return OrderQueryResponse.builder().
                        thirdCode(alipayTradeQueryResponse.getCode() + "#" + alipayTradeQueryResponse.getSubCode()).thirdMsg(alipayTradeQueryResponse.getMsg() + "#" + alipayTradeQueryResponse.getSubMsg()).build();

            }
        } catch (Exception e) {
            logger.error("执行【{}】支付宝订单状态查询出错{}", orderId, e);
            return OrderQueryResponse.builder().thirdCode("5000").thirdMsg(e.getMessage()).build();
        }
    }


}
