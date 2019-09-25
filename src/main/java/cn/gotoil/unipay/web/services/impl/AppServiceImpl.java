package cn.gotoil.unipay.web.services.impl;


import cn.gotoil.bill.web.helper.RedisHashHelper;
import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.AppExample;
import cn.gotoil.unipay.model.enums.EnumStatus;
import cn.gotoil.unipay.model.mapper.AppMapper;
import cn.gotoil.unipay.web.services.AppService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

/**
 * 应用服务实现
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、10:48
 */
@Service
public class AppServiceImpl implements AppService {

    static final Set<String> IGNORESET = Sets.newHashSet("createdAt", "updatedAt");

    @Resource
    AppMapper appMapper;

    @Autowired
    RedisTemplate redisTemplate;
    @Resource
    RedisHashHelper redisHashHelper;


    /**
     * 创建APP
     *
     * @param app
     * @return
     */
    @Override
    public int createApp(App app) {
        app.setAppSecret(RandomStringUtils.random(32, true, true));
        app.setAppKey(RandomStringUtils.random(40, true, true));
        app.setStatus(EnumStatus.Enable.getCode());
        Date d = new Date();
        app.setCreatedAt(d);
        app.setUpdatedAt(d);
        redisHashHelper.set(AppKey + app.getAppKey(), app, IGNORESET);
        return appMapper.insert(app);
    }

    /**
     * 根据appKey找对戏
     *
     * @param appKey
     * @return
     */
    @Override
    public App load(String appKey) {
        App app = redisHashHelper.get(AppKey + appKey, App.class);
        if (app != null) {
            return app;
        }
        app = appMapper.selectByPrimaryKey(appKey);
        return app;
    }

    /**
     * 判断名字是否重复
     *
     * @param name
     * @param appKey
     * @return
     */
    @Override
    public boolean nameHasExist(String name, String appKey) {
        AppExample appExample = new AppExample();
        appExample.createCriteria().andAppNameEqualTo(name).andAppKeyNotEqualTo(appKey);
        return appMapper.selectByExample(appExample).size() != 0;
    }


    /**
     * 根据Key 找secret
     *
     * @param appKey
     * @return
     */
    @Override
    public String key(String appKey) {
        App app = load(appKey);
        return app == null ? "" : app.getAppSecret();
    }

}
