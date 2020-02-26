package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.entity.Refund;
import cn.gotoil.unipay.model.enums.EnumOrderStatus;
import cn.gotoil.unipay.model.enums.EnumRefundStatus;
import cn.gotoil.unipay.model.mapper.RefundMapper;
import cn.gotoil.unipay.utils.UtilMoney;
import cn.gotoil.unipay.utils.UtilPageRedirect;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.web.message.request.ContinuePayRequest;
import cn.gotoil.unipay.web.message.response.OrderQueryResponse;
import cn.gotoil.unipay.web.message.response.OrderRefundResponse;
import cn.gotoil.unipay.web.message.response.RefundQueryResponse;
import cn.gotoil.unipay.web.services.AlipayService;
import cn.gotoil.unipay.web.services.OrderService;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.google.common.base.Charsets;
import com.google.common.net.UrlEscapers;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

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

    @Autowired
    OrderService orderService;

    @Resource
    RefundMapper refundMapper;


    /**
     * 页面支付
     *
     * @param order
     * @param chargeConfig
     * @param httpServletRequest
     * @param httpServletResponse
     * @param continuePayRequest
     * @param needSave
     * @return
     */
    @Override
    public ModelAndView pagePay(Order order, ChargeAccount chargeConfig, HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, ContinuePayRequest continuePayRequest,
                                boolean needSave) {


        ChargeAlipayModel chargeModel = (ChargeAlipayModel) chargeConfig;

        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAYURL, chargeModel.getAppID(),
                chargeModel.getPriKey(), FORMAT, Charsets.UTF_8.name(), chargeModel.getPubKey(), SIGNTYPE);


        AlipayTradeWapPayRequest alipayTradeWapPayRequest = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel alipayTradeWapPayModel = new AlipayTradeWapPayModel();
        alipayTradeWapPayModel.setOutTradeNo(order.getId());
        alipayTradeWapPayModel.setBody(UrlEscapers.urlFormParameterEscaper().escape(order.getDescp()));
        alipayTradeWapPayModel.setSubject(UrlEscapers.urlFormParameterEscaper().escape(order.getSubjects()));
        alipayTradeWapPayModel.setTimeoutExpress(order.getExpiredTimeMinute() + "m");
        alipayTradeWapPayModel.setTotalAmount(UtilMoney.fenToYuan(order.getFee(), false));
        if (StringUtils.isNotEmpty(continuePayRequest.getCancelUrl())) {
            alipayTradeWapPayModel.setQuitUrl(continuePayRequest.getCancelUrl());
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
                if (needSave) {
                    int x = orderService.saveOrder(order);
                    if (x != 1) {
                        return new ModelAndView(UtilPageRedirect.makeErrorPage(UnipayError.PageRefreshError,
                                continuePayRequest.getBackUrl()));
                    }
                }
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
     * @param order
     * @param chargeConfig
     * @return
     */
    @Override
    public String sdkPay(Order order, ChargeAccount chargeConfig) {
        ChargeAlipayModel chargeModel = (ChargeAlipayModel) chargeConfig;
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAYURL, chargeModel.getAppID(),
                chargeModel.getPriKey(), FORMAT, Charsets.UTF_8.name(), chargeModel.getPubKey(), SIGNTYPE);
        try {
            //去支付
            AlipayTradeAppPayRequest alipayTradeAppPayRequest = new AlipayTradeAppPayRequest();
            AlipayTradeAppPayModel alipayTradeAppPayModel = new AlipayTradeAppPayModel();
            alipayTradeAppPayModel.setBody(order.getDescp());
            alipayTradeAppPayModel.setSubject(order.getSubjects());
            alipayTradeAppPayModel.setOutTradeNo(order.getId());
            alipayTradeAppPayModel.setTimeoutExpress(order.getExpiredTimeMinute() + "m");
            alipayTradeAppPayModel.setTotalAmount(UtilMoney.fenToYuan(order.getFee(), false));
            alipayTradeAppPayRequest.setBizModel(alipayTradeAppPayModel);
            alipayTradeAppPayRequest.setNotifyUrl(domain + "/payment/alipay/" + order.getId());
            alipayTradeAppPayRequest.setReturnUrl(domain + "/payment/alipay/return/" + order.getId());
            AlipayTradeAppPayResponse alipayTradeAppPayResponse = alipayClient.sdkExecute(alipayTradeAppPayRequest);
            if (alipayTradeAppPayResponse.isSuccess()) {
                logger.debug("支付宝创建订单【{}】成功", order.getId());
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
        AlipayClient client = new DefaultAlipayClient(GATEWAYURL, chargeModel.getAppID(), chargeModel.getPriKey(),
                FORMAT, Charsets.UTF_8.name(), chargeModel.getPubKey(), SIGNTYPE);
        try {
            AlipayTradeQueryResponse alipayTradeQueryResponse = client.execute(alipay_request);
            if (alipayTradeQueryResponse.isSuccess()) {
                String tradeStatus = alipayTradeQueryResponse.getTradeStatus();

                OrderQueryResponse orderQueryResponse =
                        OrderQueryResponse.builder().unionOrderID(order.getId()).appOrderNO(order.getAppOrderNo()).paymentId(alipayTradeQueryResponse.getOutTradeNo()).orderFee(UtilMoney.yuanToFen(alipayTradeQueryResponse.getTotalAmount())).payFee(UtilMoney.yuanToFen(alipayTradeQueryResponse.getReceiptAmount())).thirdStatus(alipayTradeQueryResponse.getTradeStatus()).thirdCode(alipayTradeQueryResponse.getCode()).thirdMsg(alipayTradeQueryResponse.getMsg()).build();
                //                交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS
                // （交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
                if ("WAIT_BUYER_PAY".equalsIgnoreCase(tradeStatus)) {
                    orderQueryResponse.setStatus(EnumOrderStatus.Created.getCode());
                } else if ("TRADE_CLOSED".equalsIgnoreCase(tradeStatus)) {
                    orderQueryResponse.setStatus(EnumOrderStatus.PayFailed.getCode());
                } else if ("TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus)) {
                    orderQueryResponse.setPaymentId(alipayTradeQueryResponse.getTradeNo());
                    orderQueryResponse.setPaymentUid(alipayTradeQueryResponse.getBuyerLogonId());
                    orderQueryResponse.setPayFee(StringUtils.isEmpty(alipayTradeQueryResponse.getBuyerPayAmount()) ?
                            UtilMoney.yuanToFen(alipayTradeQueryResponse.getTotalAmount()) :
                            UtilMoney.yuanToFen(alipayTradeQueryResponse.getBuyerPayAmount()));
                    orderQueryResponse.setArrFee(StringUtils.isEmpty(alipayTradeQueryResponse.getReceiptAmount()) ?
                            UtilMoney.yuanToFen(alipayTradeQueryResponse.getTotalAmount()) :
                            UtilMoney.yuanToFen(alipayTradeQueryResponse.getReceiptAmount()));
                    if (null != alipayTradeQueryResponse.getSendPayDate()) {
                        orderQueryResponse.setPayDateTime(alipayTradeQueryResponse.getSendPayDate().toInstant().getEpochSecond());
                    }
                    orderQueryResponse.setStatus(EnumOrderStatus.PaySuccess.getCode());
                }
                return orderQueryResponse;

            } else if ("40004".equals(alipayTradeQueryResponse.getCode()) && "ACQ.TRADE_NOT_EXIST".equals(alipayTradeQueryResponse.getSubCode())) {
                // 在用户输入正确的支付密码前，订单都是不存在 那么 就应该判断是否真过期
                Date flagDate = DateUtils.addMinutes(order.getCreatedAt(), order.getExpiredTimeMinute() + 30);
                if (flagDate.getTime() < System.currentTimeMillis()) {
                    return OrderQueryResponse.builder().unionOrderID(order.getId()).appOrderNO(order.getAppOrderNo()).paymentId(alipayTradeQueryResponse.getOutTradeNo()).orderFee(0).payFee(0).thirdStatus(alipayTradeQueryResponse.getTradeStatus()).thirdCode(alipayTradeQueryResponse.getCode()).status(EnumOrderStatus.PayFailed.getCode()).thirdMsg(alipayTradeQueryResponse.getMsg()).build();
                } else {
                    return OrderQueryResponse.builder().unionOrderID(order.getId()).appOrderNO(order.getAppOrderNo()).paymentId(alipayTradeQueryResponse.getOutTradeNo()).orderFee(0).payFee(0).thirdStatus(alipayTradeQueryResponse.getTradeStatus()).thirdCode(alipayTradeQueryResponse.getCode()).status(EnumOrderStatus.Created.getCode()).thirdMsg(alipayTradeQueryResponse.getMsg()).build();
                }
            } else {
                logger.error("执行【{}】支付宝订单状态查询失败{}", order.getId(), JSONObject.toJSONString(alipayTradeQueryResponse));
                return OrderQueryResponse.builder().appOrderNO(order.getAppOrderNo()).
                        thirdCode(alipayTradeQueryResponse.getCode() + "#" + alipayTradeQueryResponse.getSubCode()).thirdMsg(alipayTradeQueryResponse.getMsg() + "#" + alipayTradeQueryResponse.getSubMsg()).build();

            }
        } catch (Exception e) {
            logger.error("执行【{}】支付宝订单状态查询出错{}", order.getId(), e);
            return OrderQueryResponse.builder().appOrderNO(order.getAppOrderNo()).thirdCode("5000").thirdMsg(e.getMessage()).build();
        }
    }

    /**
     * 退款申请
     *
     * @param chargeConfig
     * @param refund
     * @return
     */
    @Override
    public OrderRefundResponse orderRefund(ChargeAccount chargeConfig, Refund refund) {
        ChargeAlipayModel chargeModel = (ChargeAlipayModel) chargeConfig;
        AlipayClient client = new DefaultAlipayClient(GATEWAYURL, chargeModel.getAppID(), chargeModel.getPriKey(),
                FORMAT, Charsets.UTF_8.name(), chargeModel.getPubKey(), SIGNTYPE);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(refund.getOrderId());
        model.setRefundAmount(UtilMoney.fenToYuan(refund.getApplyFee(), false));
        model.setRefundReason(refund.getDescp());
        model.setOutRequestNo(refund.getRefundOrderId());
        request.setBizModel(model);
        try {
            AlipayTradeRefundResponse response = client.execute(request);
            if (response.isSuccess() && "Y".equalsIgnoreCase(response.getFundChange())) {
                refund.setProcessResult(EnumRefundStatus.WaitSure.getCode());
                refund.setStatusUpdateDatetime(new Date());
                refund.setUpdateAt(refund.getStatusUpdateDatetime());
                OrderRefundResponse orderRefundResponse = new OrderRefundResponse();
                orderRefundResponse.setRefundStatus(EnumRefundStatus.Refunding.getCode());
                orderRefundResponse.setReslutQueryId(refund.getRefundOrderId());
                orderRefundResponse.setMsg("退款申请成功，请调用查询退款获取退款结果");
                return orderRefundResponse;
            } else {
                refund.setFailMsg(UtilString.getLongString(response.getMsg() + response.getSubMsg(), 199));
                OrderRefundResponse orderRefundResponse = new OrderRefundResponse();
                orderRefundResponse.setRefundStatus(EnumRefundStatus.Refunding.getCode());
                orderRefundResponse.setReslutQueryId(refund.getRefundOrderId());
                refund.setProcessResult(EnumRefundStatus.Failed.getCode());
                orderRefundResponse.setMsg("退款申请失败:" + response.getMsg() + response.getSubMsg());
                return orderRefundResponse;
            }
        } catch (AlipayApiException e) {
            throw new BillException(55, e.getErrCode());
        } finally {
            refundMapper.updateByPrimaryKey(refund);
        }
    }

    /**
     * 退款状态查询
     *
     * @param chargeConfig
     * @param refund
     * @return
     */
    @Override
    public RefundQueryResponse orderRefundQuery(ChargeAccount chargeConfig, Refund refund) {
        ChargeAlipayModel chargeModel = (ChargeAlipayModel) chargeConfig;
        AlipayClient client = new DefaultAlipayClient(GATEWAYURL, chargeModel.getAppID(), chargeModel.getPriKey(),
                FORMAT, Charsets.UTF_8.name(), chargeModel.getPubKey(), SIGNTYPE);
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo(refund.getOrderId());
        model.setOutRequestNo(refund.getRefundOrderId());
        request.setBizModel(model);
        try {
            AlipayTradeFastpayRefundQueryResponse response = client.execute(request);
            if (response.isSuccess()) {
                RefundQueryResponse refundQueryResponse =
                        RefundQueryResponse.builder().orderRefundId(refund.getRefundOrderId()).orderId(refund.getOrderId()).appOrderNo(refund.getAppOrderNo()).appOrderRefundNo(refund.getAppOrderRefundNo()).applyFee(refund.getApplyFee()).thirdMsg(response.getMsg() + response.getSubMsg()).thirdCode(response.getCode() + response.getSubCode()).build();
                //如果该接口返回了查询数据，且refund_status为空或为REFUND_SUCCESS，则代表退款成功
                if (model.getOutTradeNo().equals(response.getOutTradeNo()) && model.getOutRequestNo().equals(response.getOutRequestNo()) && (StringUtils.isEmpty(response.getRefundStatus()) || "REFUND_SUCCESS".equalsIgnoreCase(response.getRefundStatus()))) {
                    refundQueryResponse.setPassFee(UtilMoney.yuanToFen(response.getRefundAmount()));
                    refundQueryResponse.setRefundStatus(EnumRefundStatus.Success.getCode());
                    return refundQueryResponse;
                } else {
                    refundQueryResponse.setPassFee(0);
                    refundQueryResponse.setRefundStatus(EnumRefundStatus.WaitSure.getCode());
                }
                return refundQueryResponse;
            } else {
                throw new BillException(5003,
                        response.getSubCode() + response.getErrorCode() + response.getMsg() + response.getSubMsg());
            }
        } catch (AlipayApiException e) {
            throw new BillException(55, e.getErrCode());
        }
    }
}
