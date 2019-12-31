package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.AppAccountIds;
import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.admin.AppListRequest;

import java.util.List;
import java.util.Map;

/**
 * 应用服务
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、10:47
 */
public interface AppService {

    String APPKEY = "Unipay:App:";

    /**
     * 创建APP
     *
     * @param app
     * @return
     */
    int createApp(App app);

    /**
     * 根据appKey找对戏
     *
     * @param appKey
     * @return
     */
    App load(String appKey);

    /**
     * 判断名字是否重复
     *
     * @param name
     * @param appKey
     * @return
     */
    boolean nameHasExist(String name, String appKey);

    /**
     * 根据Key 找secret
     *
     * @param appKey
     * @return
     */
    String key(String appKey);

    /**
     * 根据枚举类型获取收款帐号
     *
     * @param payType
     * @return
     */
    List queryAllAccount(String payType);

    BasePageResponse queryApps(AppListRequest appListRequest);

    /**
     * 更新APP状态
     *
     * @param appkey
     * @param status
     * @return
     */
    Object updateStatus(String appkey, Byte status);

    /**
     * 更新APP
     *
     * @param app
     * @return
     */
    int updateApp(App app);

    /**
     * 查询有效APP
     *
     * @return
     */
    List<App> getApps(boolean includeDisabled);

    /**
     * 根据APPKEY获取配置的收款账号
     *
     * @param appkey
     * @return
     */
    Map getByAppkey(String appkey);

    /**
     * 应用支付方式授权
     * @param appAccountIds
     * @return
     */
    boolean grantPay(AppAccountIds appAccountIds);

    /**
     * 刷新应用收款账号关系
     * @return
     */
    int refreshAppChargeAccountRedis();

    /**
     * 刷新APP
     *
     * @return
     */
    int refreshApp();
}
