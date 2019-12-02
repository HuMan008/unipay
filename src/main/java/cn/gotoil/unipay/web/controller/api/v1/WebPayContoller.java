package cn.gotoil.unipay.web.controller.api.v1;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.exception.CommonError;
import cn.gotoil.bill.tools.ObjectHelper;
import cn.gotoil.bill.tools.encoder.Hash;
import cn.gotoil.bill.tools.encoder.Hmac;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.utils.UtilBase64;
import cn.gotoil.unipay.utils.UtilMySign;
import cn.gotoil.unipay.utils.UtilRequest;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.web.message.request.PayRequest;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.SignatureUtils;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.time.Instant;
import java.util.Map;
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

    @Autowired
    WechatService wechatService;

    @Autowired
    AlipayService alipayService;

    @Autowired
    AppService appService;

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
                return new ModelAndView(UtilString.makeErrorPage(UnipayError.IllegalRequest, payRequest.getBackUrl()));
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
            switch (payType) {
                case WechatH5: {
                    ChargeWechatModel chargeWechatModel =
                            JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                    ChargeWechatModel.class);
                    return wechatService.pagePay(payRequest, order, chargeWechatModel, httpServletRequest,
                            httpServletResponse);
                }
                case WechatJSAPI: {
                    ChargeWechatModel chargeWechatModel =
                            JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                    ChargeWechatModel.class);
                    //未传递Openid
                    if (StringUtils.isEmpty(payRequest.getPaymentUserID())) {
                        String param = UtilBase64.encode(ObjectHelper.jsonString(payRequest).getBytes()).replaceAll(
                                "\\+", "GT680");
                        long time = Instant.now().getEpochSecond();
                        String path = "";
                        try {
                            URL url = new URL(wechat_open_id_grant_url);
                            path = url.getPath();
                        } catch (MalformedURLException E) {
                            log.error("无效的jump地址");
                        }
                        TreeMap<String, String> map = new TreeMap<>();
                        map.put("redirect", domain + "/web/afterwechatgrant?param=" + param);
                        map.put("app_id", wechat_open_id_grant_id);
                        map.put("s_time", String.valueOf(time));
                        String paramString = UtilMySign.makeSignStr(map);
                        String signStr =
                                String.format(path,chargeWechatModel.getAppID()) + "|" + wechat_open_id_grant_id +
                                        "|" + time + "|{" + paramString+"}";
                        String sign = Hmac.SHA1(signStr, wechat_open_id_grant_key);
//                        wechat_open_id_grant_url: "http://thirdparty.guotongshiyou.cn/third_party/oauth/wechat/%s?app_id=%s&sign=%s&s_time=%s&redirect=%s"
                        String redirectUrlP = String.format(wechat_open_id_grant_url,
                               chargeWechatModel.getAppID(),wechat_open_id_grant_id,
                                sign, time,domain + "/web/afterwechatgrant?param=" + param);
                        try {
                            //这里转发了，后面没事干了。这个时候订单还没保存
                            httpServletResponse.sendRedirect(redirectUrlP);
                            return null;
                        } catch (IOException e) {
                            log.error("获取微信OPEI跳转过程中出错{}", e.getMessage());
                            return new ModelAndView(UtilString.makeErrorPage(CommonError.SystemError,
                                    payRequest.getBackUrl()));
                        }
                    }
                    return wechatService.pagePay(payRequest, order, chargeWechatModel, httpServletRequest,
                            httpServletResponse);
                }
                case AlipayH5:
                    ChargeAlipayModel chargeAlipayModel =
                            JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                    ChargeAlipayModel.class);
                    orderService.saveOrder(order);
                    return alipayService.pagePay(payRequest, order, chargeAlipayModel, httpServletRequest,
                            httpServletResponse);
                default:
                    throw new BillException(UnipayError.PayTypeNotImpl);
            }
        } catch (BillException e) {
            return new ModelAndView(UtilString.makeErrorPage(e.getTickcode(), e.getMessage(), payRequest.getBackUrl()));
        } catch (Exception e) {
            return new ModelAndView(UtilString.makeErrorPage(CommonError.SystemError, payRequest.getBackUrl()));
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
                httpServletRequest.getRequestURI() + "|" + wechat_open_id_grant_id + "|" + s_time + "|{" +UtilMySign.makeSignStr(map)+
                        "}";
        String mySign = Hmac.SHA1(playLoadStr, wechat_open_id_grant_key);

        if (!mySign.equals(sign)) {
            return new ModelAndView(UtilString.makeErrorPage(5000, "Jumper验签失败", ""));
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
