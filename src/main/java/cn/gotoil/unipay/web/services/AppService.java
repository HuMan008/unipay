package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.entity.App;

/**
 * 应用服务
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、10:47
 */
public interface AppService {

    String AppKey = "Unipay:App:";

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
}
