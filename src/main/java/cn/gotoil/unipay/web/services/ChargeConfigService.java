package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.entity.ChargeConfig;

/**
 * 应用收款账号及配置关系接口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、16:02
 */
public interface ChargeConfigService {
    /**
     * 根据appid,收款类型，找具体的收款账号对象
     *
     * @param appId
     * @param payType
     * @return
     */
    ChargeConfig loadByAppIdPayType(String appId, String payType);
}
