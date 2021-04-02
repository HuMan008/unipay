package cn.gotoil.unipay.classes;

import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.ChargeWechatV2Model;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.web.services.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author think <syj247@qq.com>、
 * @date 2020-4-1、14:51
 */
@Slf4j
@Component
@ConditionalOnBean(value = {WechatService.class, AlipayService.class, WechatV2Service.class})
public class PayDispatcher {

    @Autowired
    AlipayService alipayService;
    @Autowired
    WechatService wechatService;
    @Autowired
    WechatV2Service wechatV2Service;


    public final static Set<EnumPayType> SDK_PAY_SET = new HashSet<>();

    public final static Set<EnumPayType> PAGE_PAY_SET = new HashSet<>();

    static{
        SDK_PAY_SET.add(EnumPayType.WechatSDK);
        SDK_PAY_SET.add(EnumPayType.AlipaySDK);
        SDK_PAY_SET.add(EnumPayType.WechatNAtive);

        PAGE_PAY_SET.add(EnumPayType.AlipayH5);
        PAGE_PAY_SET.add(EnumPayType.WechatH5);
        PAGE_PAY_SET.add(EnumPayType.WechatJSAPI);
    }


    public BasePayService payServerDispatcher(EnumPayType payType, String apiVersion) {
        if (StringUtils.isEmpty(apiVersion) || "v1".equalsIgnoreCase(apiVersion) || "v1.0".equalsIgnoreCase(apiVersion)) {
            if (EnumPayType.AlipaySDK.equals(payType) || EnumPayType.AlipayH5.equals(payType)) {
                return alipayService;
            } else if (EnumPayType.WechatSDK.equals(payType) || EnumPayType.WechatH5.equals(payType) || EnumPayType.WechatJSAPI.equals(payType) || EnumPayType.WechatNAtive.equals(payType)) {
                return wechatService;
            } else {
                return null;
            }
        } else if ("v2".equalsIgnoreCase(apiVersion)) {
            if (EnumPayType.AlipaySDK.equals(payType) || EnumPayType.AlipayH5.equals(payType)) {
                return alipayService;
            } else if (EnumPayType.WechatSDK.equals(payType) || EnumPayType.WechatH5.equals(payType) || EnumPayType.WechatJSAPI.equals(payType) || EnumPayType.WechatNAtive.equals(payType)) {
                return wechatV2Service;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 根据收款账号配置  返回实际收款账号配置
     *
     * @param chargeConfig
     * @return
     */

    public ChargeAccount getChargeAccountBean(ChargeConfig chargeConfig, String apiVsersion) {
        EnumPayType payType = EnumUtils.getEnum(EnumPayType.class, chargeConfig.getPayType());
        if (StringUtils.isEmpty(apiVsersion) || OrderService.APIVERSIONV1.equalsIgnoreCase(apiVsersion) || OrderService.APIVERSIONV_OLD.equalsIgnoreCase(apiVsersion)) {
            if (EnumPayType.AlipaySDK.equals(payType) || EnumPayType.AlipayH5.equals(payType)) {
                ChargeAlipayModel chargeAlipayModel =
                        JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                ChargeAlipayModel.class);
                return chargeAlipayModel;
            } else if (EnumPayType.WechatSDK.equals(payType) || EnumPayType.WechatH5.equals(payType) || EnumPayType.WechatJSAPI.equals(payType) || EnumPayType.WechatNAtive.equals(payType)) {
                ChargeWechatModel chargeWechatModel =
                        JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                ChargeWechatModel.class);
                chargeWechatModel.setMySpaceId(chargeWechatModel.getAppID());
                return chargeWechatModel;
            } else {
                return null;
            }
        } else if (OrderService.APIVERSIONV2.equalsIgnoreCase(apiVsersion)) {
            if (EnumPayType.AlipaySDK.equals(payType) || EnumPayType.AlipayH5.equals(payType)) {
                ChargeAlipayModel chargeAlipayModel =
                        JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                ChargeAlipayModel.class);
                return chargeAlipayModel;
            } else if (EnumPayType.WechatSDK.equals(payType) || EnumPayType.WechatH5.equals(payType) || EnumPayType.WechatJSAPI.equals(payType) || EnumPayType.WechatNAtive.equals(payType)) {
                ChargeWechatV2Model chargeWechatModel =
                        JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                                ChargeWechatV2Model.class);
                chargeWechatModel.setMySpaceId(chargeWechatModel.getAppId());
                return chargeWechatModel;
            } else {
                return null;
            }
        }
        return null;
    }
}
