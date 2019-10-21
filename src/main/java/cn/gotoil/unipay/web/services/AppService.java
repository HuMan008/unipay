package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.AppAccountIds;
import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.admin.AppListRequest;

import java.util.List;

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
    int createApp(App app, AppAccountIds appAccountIds);

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
     * @param appAccountIds
     * @return
     */
    int updateApp(App app, AppAccountIds appAccountIds);

    /**
     * 查询有效APP
     *
     * @return
     */
    List getApps();
}
