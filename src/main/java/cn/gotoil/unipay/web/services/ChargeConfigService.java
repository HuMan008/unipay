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
    /**
     * 根据条件查询收款账号
     *
     * @param accountName
     * @param payType
     * @param status
     * @return
     */
    List queryAccounts(String accountName, String payType, String status);

    void addAppChargeAccount2Redis(AppChargeAccount appChargeAccount);

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

    /**
     * 配置应用收款信息
     *
     * @param appkeys
     * @param accountId
     */
    void setAppAndAccount(String[] appkeys, String accountId);
}
