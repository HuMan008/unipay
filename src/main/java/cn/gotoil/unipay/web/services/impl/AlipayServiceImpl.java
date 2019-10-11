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
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
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

            AlipayTradeWapPayResponse alipayTradeWapPayResponse = alipayClient.pageExecute(alipayTradeWapPayRequest);
            if (alipayTradeWapPayResponse.isSuccess()) {
                modelAndView.addObject("from", alipayTradeWapPayResponse.getBody());
            } else {
                modelAndView.addObject("errorCode", 5000);
                modelAndView.addObject("errorMsg", "下单失败");
            }


        } catch (AlipayApiException e) {
            logger.error("{}", e);
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
        try {
            //去支付
            AlipayTradeAppPayRequest alipayTradeAppPayRequest = new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel alipayTradeAppPayModel = new AlipayTradeAppPayModel();
            alipayTradeAppPayModel.setBody(order.getDescp());
            alipayTradeAppPayModel.setSubject(order.getSubjects());
            alipayTradeAppPayModel.setOutTradeNo(order.getId());
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
                return "";
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝出错了{}【{}】", e.getErrCode(), e.getErrMsg());
            throw new BillException(e.hashCode(), e.getErrCode() + "【" + e.getErrMsg() + "】");
        }
    }

    /**
     * 订单支付状态查询 远程查
     * <p>
     * thirdCode = 5000的时候表示查询异常
     *
     * @param order
     * @param chargeConfig
     */
    @Override
    public OrderQueryResponse orderQueryFromRemote(Order order, ChargeAccount chargeConfig) {
        ChargeAlipayModel chargeModel = (ChargeAlipayModel) chargeConfig;
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(order.getId());
        alipay_request.setBizModel(model);
        AlipayClient client = new DefaultAlipayClient(GateWayURL, chargeModel.getAppID(), chargeModel.getPriKey(),
                Format, Charsets.UTF_8.name(), chargeModel.getPubKey(), SignType);
        try {
            AlipayTradeQueryResponse alipayTradeQueryResponse = client.execute(alipay_request);
            if (alipayTradeQueryResponse.isSuccess()) {
                String tradeStatus = alipayTradeQueryResponse.getTradeStatus();

                OrderQueryResponse orderQueryResponse =
                        OrderQueryResponse.builder().unionOrderID(order.getId()).paymentId(alipayTradeQueryResponse.getOutTradeNo()).orderFee(UtilMoney.yuanToFen(alipayTradeQueryResponse.getTotalAmount())).payFee(UtilMoney.yuanToFen(alipayTradeQueryResponse.getBuyerPayAmount())).thirdStatus(alipayTradeQueryResponse.getTradeStatus()).thirdCode(alipayTradeQueryResponse.getCode()).thirdMsg(alipayTradeQueryResponse.getMsg()).build();
                //                交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS
                // （交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
                if ("WAIT_BUYER_PAY".equalsIgnoreCase(tradeStatus)) {
                    orderQueryResponse.setStatus(EnumOrderStatus.Created.getCode());
                } else if ("TRADE_CLOSED".equalsIgnoreCase(tradeStatus)) {
                    orderQueryResponse.setStatus(EnumOrderStatus.PayFailed.getCode());
                } else if ("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
                    orderQueryResponse.setPaymentId(alipayTradeQueryResponse.getTradeNo());
                    orderQueryResponse.setPaymentUid(alipayTradeQueryResponse.getBuyerUserId());
                    orderQueryResponse.setPayFee(StringUtils.isEmpty(alipayTradeQueryResponse.getBuyerPayAmount()) ?
                            UtilMoney.yuanToFen(alipayTradeQueryResponse.getTotalAmount()) :
                            UtilMoney.yuanToFen(alipayTradeQueryResponse.getBuyerPayAmount()));
                    if (null != alipayTradeQueryResponse.getSendPayDate()) {
                        orderQueryResponse.setPayDateTime(alipayTradeQueryResponse.getSendPayDate().toInstant().getEpochSecond());
                    }
                    orderQueryResponse.setStatus(EnumOrderStatus.PaySuccess.getCode());
                }
                return orderQueryResponse;

            } else if ("40004".equals(alipayTradeQueryResponse.getCode()) && "ACQ.TRADE_NOT_EXIST".equals(alipayTradeQueryResponse.getSubCode())) {
                return OrderQueryResponse.builder().unionOrderID(order.getId()).paymentId(alipayTradeQueryResponse.getOutTradeNo()).orderFee(0).payFee(0).thirdStatus(alipayTradeQueryResponse.getTradeStatus()).thirdCode(alipayTradeQueryResponse.getCode()).status(EnumOrderStatus.Created.getCode()).thirdMsg(alipayTradeQueryResponse.getMsg()).build();
            } else {
                logger.error("执行【{}】支付宝订单状态查询失败{}", order.getId(), JSONObject.toJSONString(alipayTradeQueryResponse));
                return OrderQueryResponse.builder().
                        thirdCode(alipayTradeQueryResponse.getCode() + "#" + alipayTradeQueryResponse.getSubCode()).thirdMsg(alipayTradeQueryResponse.getMsg() + "#" + alipayTradeQueryResponse.getSubMsg()).build();

            }
        } catch (Exception e) {
            logger.error("执行【{}】支付宝订单状态查询出错{}", order.getId(), e);
            return OrderQueryResponse.builder().thirdCode("5000").thirdMsg(e.getMessage()).build();
        }
    }


}
