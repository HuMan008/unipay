package cn.gotoil.unipay.web.services.impl;


import cn.gotoil.unipay.model.entity.AppChargeAccount;
import cn.gotoil.unipay.model.entity.AppChargeAccountExample;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.mapper.AppChargeAccountMapper;
import cn.gotoil.unipay.model.mapper.ChargeConfigMapper;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用收款账号配置及关系实现类
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、15:54
 */
@Service
public class ChargeConfigServiceImpl implements ChargeConfigService {

    @Resource
    AppChargeAccountMapper appChargeAccountMapper;
    @Resource
    ChargeConfigMapper chargeConfigMapper;

    /**
     * 根据appid,收款类型，找具体的收款账号对象
     *
     * @param appId
     * @param payType
     * @return
     */
    @Override
    public ChargeConfig loadByAppIdPayType(String appId, String payType) {
        AppChargeAccountExample appChargeAccountExample = new AppChargeAccountExample();
        appChargeAccountExample.createCriteria().andPayTypeEqualTo(payType).andAppIdEqualTo(appId);
        List<AppChargeAccount> appChargeAccountList = appChargeAccountMapper.selectByExample(appChargeAccountExample);
        if (1 != appChargeAccountList.size()) {
            return null;
        }
        AppChargeAccount appChargeAccount = appChargeAccountList.get(0);
        int accountId = appChargeAccount.getAccountId();
        return chargeConfigMapper.selectByPrimaryKey(accountId);
    }

    /**
     * 根据账号ID加载对象
     *
     * @param configId
     * @return
     */
    @Override
    public ChargeConfig loadByChargeId(int configId) {
        return chargeConfigMapper.selectByPrimaryKey(configId);
    }
}
