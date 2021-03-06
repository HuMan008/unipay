package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.entity.AppChargeAccount;
import cn.gotoil.unipay.model.entity.ChargeConfig;

import java.util.List;

/**
 * 应用收款账号及配置关系接口
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、16:02
 */
public interface ChargeConfigService {

    String APPCHARGKEY = "Unipay:appCharge:";

    /**
     * 关系
     *
     * @param appId
     * @param payTypeCode
     * @return
     */
    String appChargAccountKey4AppidAndPayTypeRedisKey(String appId, String payTypeCode);

    /**
     * 根据条件查询收款账号
     *
     * @param accountName
     * @param payType
     * @param status
     * @return
     */
    List<ChargeConfig> queryAccounts(String accountName, String payType, String status);

    void addAppChargeAccount2Redis(AppChargeAccount appChargeAccount);

    /**
     * 找应用找所有的支持的支付方式关系
     * @param appId
     * @return
     */
    List<AppChargeAccount> getRByAppId(String appId);

    /**
     * 根据appid,收款类型，找具体的收款账号对象
     *
     * @param appId
     * @param payType
     * @return
     */
    ChargeConfig loadByAppIdPayType(String appId, String payType);

    /**
     * 根据账号ID加载对象
     *
     * @param configId
     * @return
     */
    ChargeConfig loadByChargeId(int configId);

    /**
     * 新增收款账号
     *
     * @param chargeConfig
     * @return
     */
    int addChargeConfig(ChargeConfig chargeConfig);

    /**
     * 更新收款账号
     *
     * @param chargeConfig
     * @return
     */
    int updateAccount(ChargeConfig chargeConfig);

    /**
     * 检查名字是否重复
     *
     * @param name
     * @param id
     * @return
     */
    boolean checkName(String name, Integer id);

    /**
     * 修改状态
     *
     * @param id
     * @param status
     * @return
     */
    boolean updateStatus(Integer id, Byte status);

    /**
     * 刷新账号配置
     *
     * @return
     */
    int refreshAccount();

}
