package cn.gotoil.unipay.classes;

import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.web.services.AlipayService;
import cn.gotoil.unipay.web.services.BasePayService;
import cn.gotoil.unipay.web.services.WechatService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author think <syj247@qq.com>、
 * @date 2020-4-1、14:51
 */
@Slf4j
@Component
public class PayDispatcher {

    @Autowired
    AlipayService alipayService;
    @Autowired
    WechatService wechatService;


    public final static Set<EnumPayType> sdkPaySet = new HashSet<>();

    public final static Set<EnumPayType> pagePaySet = new HashSet<>();

    static{
        sdkPaySet.add(EnumPayType.WechatSDK);
        sdkPaySet.add(EnumPayType.AlipaySDK);
        sdkPaySet.add(EnumPayType.WechatNAtive);

        pagePaySet.add(EnumPayType.AlipayH5);
        pagePaySet.add(EnumPayType.WechatH5);
        pagePaySet.add(EnumPayType.WechatJSAPI);
    }

    public BasePayService payServerDispatcher(EnumPayType payType){
        if(EnumPayType.AlipaySDK.equals(payType) || EnumPayType.AlipayH5.equals(payType)){
            return alipayService;
        }else if(EnumPayType.WechatSDK.equals(payType) || EnumPayType.WechatH5.equals(payType) || EnumPayType.WechatJSAPI.equals(payType) || EnumPayType.WechatNAtive.equals(payType)){
            return wechatService;
        }else{
            return null;
        }
    }

    /**
     * 根据收款账号配置  返回实际收款账号配置
     *
     * @param chargeConfig
     * @return
     */
    public ChargeAccount getChargeAccountBean(ChargeConfig chargeConfig) {
        EnumPayType payType = EnumUtils.getEnum(EnumPayType.class, chargeConfig.getPayType());
        if(EnumPayType.AlipaySDK.equals(payType) || EnumPayType.AlipayH5.equals(payType)){
            ChargeAlipayModel chargeAlipayModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                            ChargeAlipayModel.class);
            return  chargeAlipayModel;
        }else if(EnumPayType.WechatSDK.equals(payType) || EnumPayType.WechatH5.equals(payType) || EnumPayType.WechatJSAPI.equals(payType) || EnumPayType.WechatNAtive.equals(payType)){
            ChargeWechatModel chargeWechatModel =
                    JSONObject.toJavaObject((JSON) JSON.parse(chargeConfig.getConfigJson()),
                            ChargeWechatModel.class);
            return  chargeWechatModel;
        }else{
            return null;
        }
    }
}
