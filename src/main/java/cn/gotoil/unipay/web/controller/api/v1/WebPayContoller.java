package cn.gotoil.unipay.web.controller.api.v1;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.exception.CommonError;
import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.bill.tools.encoder.Hmac;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.classes.PayDispatcher;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.utils.*;
import cn.gotoil.unipay.web.message.request.ContinuePayRequest;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.Instant;
import java.util.Optional;
import java.util.TreeMap;

/**
 * 页面支付入口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-30、15:38
 */
@Controller
@RequestMapping("/web")
@Slf4j
public class WebPayContoller {

    @Autowired
    OrderService orderService;

    @Autowired
    ChargeConfigService chargeConfigService;

//    @Autowired
//    WechatService wechatService;
//
//    @Autowired
//    AlipayService alipayService;

    @Autowired
    AppService appService;

    @Autowired
    PayDispatcher payDispatcher;

    @Value("${domain}")
    String domain;
    @Value("${wechat_open_id_grant_url}")
    String wechat_open_id_grant_url;
    @Value("${wechat_open_id_grant_id}")
    String wechat_open_id_grant_id;
    @Value("${wechat_open_id_grant_key}")
    String wechat_open_id_grant_key;
    @Value("${isDebug}")
    private boolean isDebug;

    @NeedLogin(value = false)
    @RequestMapping(value = "dopay", method = RequestMethod.GET)
    @ApiOperation(value = "Web订单创建", position = 5)

    public ModelAndView createOrder(PayRequest payRequest,
                                    //    public ModelAndView createOrder(@Valid @RequstBody PayRequest payRequest,
                                    HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        //校验SIGN
        //        appId+appOrderNo+payType+fee+appKey
        if (!isDebug) {
            String signStr =
                    payRequest.getAppId() + payRequest.getAppOrderNo() + payRequest.getPayType() + payRequest.getFee() + appService.key(payRequest.getAppId());
            if (StringUtils.isEmpty(payRequest.getSign()) || !payRequest.getSign().equals(Hash.md5(signStr).toUpperCase())) {
                return new ModelAndView(UtilPageRedirect.makeErrorPage(UnipayError.IllegalRequest, payRequest.getBackUrl()));
            }
        }
        try {
            //校验请求
            orderService.checkPayRequest(payRequest);
            //填充请求 有些参数请求里没传的
            orderService.fillPayRequest(payRequest);
            //创建订单（不持久化）
            Order order = orderService.warpPayRequest2UnionOrder(payRequest);
            EnumPayType payType = EnumUtils.getEnum(EnumPayType.class, payRequest.getPayType());
            ChargeConfig chargeConfig = chargeConfigService.loadByAppIdPayType(payRequest.getAppId(),
                    payType.getCode());
            ContinuePayRequest continuePayRequest = new ContinuePayRequest();
            continuePayRequest.setAppId(payRequest.getAppId());
            continuePayRequest.setAppOrderNo(payRequest.getAppOrderNo());
            continuePayRequest.setBackUrl(payRequest.getBackUrl());
            continuePayRequest.setCancelUrl(payRequest.getCancelUrl());
            continuePayRequest.setAutoCommit(payRequest.getAutoCommit());

            if(!PayDispatcher.pagePaySet.contains(payType)){
                throw new BillException(UnipayError.PayTypeNotImpl);
            }
            BasePayService payService =payDispatcher.payServerDispatcher(payType);
            ChargeAccount chargeAccount = payDispatcher.getChargeAccountBean(chargeConfig);

            if(EnumPayType.WechatJSAPI.equals(payType) && StringUtils.isEmpty(payRequest.getPaymentUserID())){
                payService = (WechatService) payService;
                ((WechatService) payService).setPayRequest(payRequest);
                ((WechatService) payService).setMustNeedOpenId(true);
                return payService.pagePay(order,chargeAccount,httpServletRequest,httpServletResponse,
                        continuePayRequest,false);
            }else{
                return payService.pagePay(order,chargeAccount,httpServletRequest,httpServletResponse,
                        continuePayRequest,true);
            }

        } catch (BillException e) {
            return new ModelAndView(UtilPageRedirect.makeErrorPage(e.getTickcode(), e.getMessage(), payRequest.getBackUrl()));
        } catch (Exception e) {
            return new ModelAndView(UtilPageRedirect.makeErrorPage(CommonError.SystemError, payRequest.getBackUrl()));
        }
    }

    @NeedLogin(value = false)
    @RequestMapping(value = "repay", method = RequestMethod.GET)
    @ApiOperation(value = "Web订单重新支付", position = 5)
    public ModelAndView repayOrder(ContinuePayRequest continuePayRequest, HttpServletRequest httpServletRequest,
                                   HttpServletResponse httpServletResponse) {

        Order order = orderService.loadByAppOrderNo(continuePayRequest.getAppOrderNo(), continuePayRequest.getAppId());
        Optional.ofNullable(order).orElseThrow(() -> new BillException(UnipayError.OrderNotExists));

        if (!orderService.rePayOrderCheck(order)) {
            throw new BillException(UnipayError.OrdeeStatusErrorOrOrderExpirsed);
        }
        //校验SIGN
        //        appId+appOrderNo+payType+fee+appKey
        if (!isDebug) {
            String signStr =
                    order.getAppId() + order.getAppOrderNo() + order.getPayType() + order.getFee() + appService.key(order.getAppId());
            if (StringUtils.isEmpty(continuePayRequest.getSign()) || !continuePayRequest.getSign().equals(Hash.md5(signStr).toUpperCase())) {
                return new ModelAndView(UtilPageRedirect.makeErrorPage(UnipayError.IllegalRequest,
                        continuePayRequest.getBackUrl()));
            }
        }
        try {

            //校验订单是否有效

            EnumPayType payType = EnumUtils.getEnum(EnumPayType.class, order.getPayType());
            ChargeConfig chargeConfig = chargeConfigService.loadByChargeId(order.getChargeAccountId());
            ChargeAccount chargeAccount = payDispatcher.getChargeAccountBean(chargeConfig);
            BasePayService payService  = payDispatcher.payServerDispatcher(payType);
            return payService.pagePay(order,chargeAccount,httpServletRequest,httpServletResponse,continuePayRequest,
                    false);
//            switch (payType) {
//                case WechatH5: {
//                    ChargeWechatModel chargeWechatModel =
//                            JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
//                                    ChargeWechatModel.class);
//                    return wechatService.pagePay(order, chargeWechatModel, httpServletRequest, httpServletResponse,
//                            continuePayRequest, false);
//                }
//                case WechatJSAPI: {
//                    ChargeWechatModel chargeWechatModel =
//                            JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
//                                    ChargeWechatModel.class);
//                    return wechatService.pagePay(order, chargeWechatModel, httpServletRequest, httpServletResponse,
//                            continuePayRequest, false);
//                }
//                case AlipayH5:
//                    ChargeAlipayModel chargeAlipayModel =
//                            JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
//                                    ChargeAlipayModel.class);
//
//                    return alipayService.pagePay(order, chargeAlipayModel, httpServletRequest, httpServletResponse,
//                            continuePayRequest, false);
//                default:
//                    throw new BillException(UnipayError.PayTypeNotImpl);
//            }
        } catch (BillException e) {
            return new ModelAndView(UtilPageRedirect.makeErrorPage(e.getTickcode(), e.getMessage(),
                    continuePayRequest.getBackUrl()));
        } catch (Exception e) {
            return new ModelAndView(UtilPageRedirect.makeErrorPage(CommonError.SystemError, continuePayRequest.getBackUrl()));
        }
    }

    @NeedLogin(value = false)
    @RequestMapping(value = "error")
    @ApiIgnore
    public ModelAndView error(String errorCode, String errorMsg, String backUrl) {
        ModelAndView modelAndView = new ModelAndView("error/error");
        modelAndView.addObject("domain", domain);
        modelAndView.addObject("errorCode", errorCode);
        modelAndView.addObject("errorMsg", errorMsg);
        modelAndView.addObject("backUrl", backUrl);
        return modelAndView;
    }


    @RequestMapping("afterwechatgrant")
    @NeedLogin(value = false)
    @ApiIgnore
    public Object afterwechatgrant(@RequestParam String param, @RequestParam String open_id,
                                   HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                   @RequestParam String nickname, @RequestParam String avatar,
                                   @RequestParam String unionid, @RequestParam String s_time, String sign) throws Exception {
        TreeMap<String, String> map = new TreeMap<>(UtilRequest.request2Map(httpServletRequest));
        String playLoadStr =
                httpServletRequest.getRequestURI() + "|" + wechat_open_id_grant_id + "|" + s_time + "|{" + UtilMySign.makeSignStr(map) + "}";
        String mySign = Hmac.SHA1(playLoadStr, wechat_open_id_grant_key);

        if (!mySign.equals(sign)) {
            return new ModelAndView(UtilPageRedirect.makeErrorPage(5000, "Jumper验签失败", ""));
        }
        try {
            param = new String(UtilBase64.decode(param.replaceAll("GT680", "+")));
            param = URLDecoder.decode(param, Charsets.UTF_8.name());
            PayRequest payRequest = JSONObject.toJavaObject(JSONObject.parseObject(param), PayRequest.class);
            //校验请求
            payRequest.setPaymentUserID(open_id);
            //            orderService.checkPayRequest(payRequest);
            //填充请求 有些参数请求里没传的
            orderService.fillPayRequest(payRequest);

            //创建订单（不持久化）
            Order order = orderService.warpPayRequest2UnionOrder(payRequest);
            order.setPaymentUid(payRequest.getPaymentUserID());

            EnumPayType payType = EnumUtils.getEnum(EnumPayType.class, payRequest.getPayType());
            ChargeConfig chargeConfig = chargeConfigService.loadByAppIdPayType(payRequest.getAppId(),
                    payType.getCode());

            ChargeWechatModel chargeWechatModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()), ChargeWechatModel.class);
            ContinuePayRequest continuePayRequest = new ContinuePayRequest();
            continuePayRequest.setAppId(payRequest.getAppId());
            continuePayRequest.setAppOrderNo(payRequest.getAppOrderNo());
            continuePayRequest.setBackUrl(payRequest.getBackUrl());
            continuePayRequest.setCancelUrl(payRequest.getCancelUrl());
            continuePayRequest.setAutoCommit(payRequest.getAutoCommit());
            return payDispatcher.payServerDispatcher(payType).pagePay(order, chargeWechatModel, httpServletRequest,
                    httpServletResponse,
                    continuePayRequest, true);
//            return wechatService.pagePay(order, chargeWechatModel, httpServletRequest, httpServletResponse,
//                    continuePayRequest, true);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
