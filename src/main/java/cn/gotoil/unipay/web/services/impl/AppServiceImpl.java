package cn.gotoil.unipay.web.services.impl;


import cn.gotoil.bill.web.helper.RedisHashHelper;
import cn.gotoil.unipay.model.entity.*;
import cn.gotoil.unipay.model.enums.EnumPayCategory;
import cn.gotoil.unipay.model.enums.EnumStatus;
import cn.gotoil.unipay.model.mapper.AppChargeAccountMapper;
import cn.gotoil.unipay.model.mapper.AppMapper;
import cn.gotoil.unipay.model.mapper.ChargeConfigMapper;
import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.AppListRequest;
import cn.gotoil.unipay.web.services.AppService;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    @Resource
    ChargeConfigMapper chargeConfigMapper;
    @Resource
    AppChargeAccountMapper appChargeAccountMapper;


    /**
     * 创建APP
     *
     * @param app
     * @return
     */
    @Override
    public int createApp(App app, AppAccountIds appAccountIds) {
        app.setAppSecret(RandomStringUtils.random(32, true, true));
        app.setAppKey(RandomStringUtils.random(40, true, true));
        app.setStatus(EnumStatus.Enable.getCode());
        Date d = new Date();
        app.setCreatedAt(d);
        app.setUpdatedAt(d);
        redisHashHelper.set(AppKey + app.getAppKey(), app, IGNORESET);
        appChargeAccountMapper(app.getAppKey(),appAccountIds);
        return appMapper.insert(app);
    }

    private void appChargeAccountMapper(String appId, AppAccountIds appAccountIds) {
        if (appAccountIds != null) {
            created(appId, appAccountIds.getAlipayId(), EnumPayCategory.Alipay.getCode());
            created(appId, appAccountIds.getWechatId(), EnumPayCategory.Wechat.getCode());
        }
    }

    private void created(String appid, Integer payid, String code) {
        if (payid != null && -1 != payid) {
            AppChargeAccount appAC = new AppChargeAccount();
            appAC.setCreatedAt(new Date());
            appAC.setAccountId(payid);
            appAC.setAppId(appid);
            appAC.setPayType(code);
            appAC.setStatus(EnumStatus.Enable.getCode());
            appChargeAccountMapper.insert(appAC);
        }
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
        AppExample.Criteria criteria = appExample.createCriteria();
        criteria.andAppNameEqualTo(name);
        if(StringUtils.isNotEmpty(appKey)) {
            criteria.andAppKeyNotEqualTo(appKey);
        }
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

    @Override
    public List queryAllAccount(String payType) {
        ChargeConfigExample example = new ChargeConfigExample();
        example.createCriteria().andPayTypeEqualTo(payType).andStatusEqualTo(EnumStatus.Enable.getCode());
        return chargeConfigMapper.selectByExample(example);
    }

    @Override
    public BasePageResponse queryApps(AppListRequest appListRequest){
        BasePageResponse pageResponse = new BasePageResponse();
        BeanUtils.copyProperties(appListRequest, pageResponse);
        AppExample appExample = new AppExample();
        Map<String, Object> params = appListRequest.getParams();
        AppExample.Criteria criteria = appExample.createCriteria();
        if (params.containsKey("appName") && StringUtils.isNotEmpty((String) params.get("appName"))) {
            criteria.andAppNameEqualTo(String.valueOf(params.get("appName")));
        }

        if (params.containsKey("status") && StringUtils.isNotEmpty((String) params.get("status"))) {
            criteria.andStatusEqualTo(Byte.valueOf(String.valueOf(params.get("status"))));
        }

        List<App> lists = appMapper.selectByExample(appExample);
        pageResponse.setTotal(lists.isEmpty() ? 0 : lists.size());
        appExample.setLimit(pageResponse.getPageSize());
        appExample.setOffset(pageResponse.getOffset());
        appExample.setOrderByClause("created_at desc");
        pageResponse.setRows(appMapper.selectByExample(appExample));
        return pageResponse;
    }
}
