package cn.gotoil.unipay.web.services.impl;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.exception.CommonError;
import cn.gotoil.bill.web.helper.RedisHashHelper;
import cn.gotoil.unipay.model.entity.*;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.enums.EnumStatus;
import cn.gotoil.unipay.model.mapper.AppChargeAccountMapper;
import cn.gotoil.unipay.model.mapper.AppMapper;
import cn.gotoil.unipay.model.mapper.ChargeConfigMapper;
import cn.gotoil.unipay.model.mapper.ext.AppQueryMapper;
import cn.gotoil.unipay.web.annotation.OpLog;
import cn.gotoil.unipay.web.message.BasePageResponse;
import cn.gotoil.unipay.web.message.request.admin.AppListRequest;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 应用服务实现
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、10:48
 */
@Service
@Slf4j
public class AppServiceImpl implements AppService {

    public static final String APPCHARGKEY = "appCharge:";

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
    @Resource
    AppQueryMapper appQueryMapper;
    @Resource
    ChargeConfigService chargeConfigService;


    /**
     * 创建APP
     *
     * @param app
     * @return
     */
    @Override
    @OpLog(desc = "新增应用")
    public int createApp(App app) {
        app.setAppSecret(RandomStringUtils.random(40, true, true));
        app.setAppKey(RandomStringUtils.random(32, true, true));
        app.setStatus(EnumStatus.Enable.getCode());
        Date d = new Date();
        app.setCreatedAt(d);
        app.setUpdatedAt(d);
        redisHashHelper.set(APPKEY + app.getAppKey(), app, IGNORESET);
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
        App app = redisHashHelper.get(APPKEY + appKey, App.class);
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
        if (StringUtils.isNotEmpty(appKey)) {
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
    public BasePageResponse queryApps(AppListRequest appListRequest) {
        BasePageResponse pageResponse = new BasePageResponse();
        BeanUtils.copyProperties(appListRequest, pageResponse);
        AppExample appExample = new AppExample();
        Map<String, Object> params = appListRequest.getParams();
        AppExample.Criteria criteria = appExample.createCriteria();
        if (params.containsKey("appName") && StringUtils.isNotEmpty((String) params.get("appName"))) {
            criteria.andAppNameLike("%" + String.valueOf(params.get("appName")));
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

    /**
     * 更新APP状态
     *
     * @param appkey
     * @param status
     * @return
     */
    @Override
    @OpLog(desc = "更新应用状态")
    public Object updateStatus(String appkey, Byte status) {
        App old = appMapper.selectByPrimaryKey(appkey);
        if (old == null) {
            throw new BillException(9100, "找不到对应APP");
        }
        App updateApp = new App();
        updateApp.setAppKey(appkey);
        updateApp.setStatus(status);
        updateApp.setUpdatedAt(new Date());

        int x = appMapper.updateByPrimaryKeySelective(updateApp);
        if (x == 1) {
            old.setStatus(status);
            redisHashHelper.set(APPKEY + appkey, old, IGNORESET);
            return true;
        } else {
            throw new BillException(CommonError.SystemError);
        }
    }


    /**
     * 更新APP
     *
     * @param app
     * @return
     */
    @Override
    @OpLog(desc = "更新应用")
    public int updateApp(App app) {
        app.setUpdatedAt(new Date());
        int result = appMapper.updateByPrimaryKeySelective(app);

        if (result == 1) {
            App appNew = appMapper.selectByPrimaryKey(app.getAppKey());
            redisHashHelper.set(APPKEY + app.getAppKey(), appNew, IGNORESET);
        }
        return result;
    }

    private void removeAppChargeAccountFromRedis(String payType, String appId) {
        String key = ChargeConfigServiceImpl.APPCHARGKEY + payType + "_" + appId;
        redisTemplate.opsForHash().getOperations().expire(key, 0, TimeUnit.SECONDS);
    }


    /**
     * 查询有效APP
     *
     * @param includeDisabled 是否包含无效
     * @return
     */
    @Override
    public List<App> getApps(boolean includeDisabled) {
        AppExample example = new AppExample();
        if (includeDisabled) {
            example.createCriteria().andStatusEqualTo(EnumStatus.Enable.getCode());
        }
        return appMapper.selectByExample(example);
    }

    /**
     * 根据APPKEY获取配置的收款账号
     *
     * @param appkey
     * @return
     */
    @Override
    public Map getByAppkey(String appkey) {

        AppChargeAccountExample example = new AppChargeAccountExample();
        example.createCriteria().andAppIdEqualTo(appkey).andStatusEqualTo(EnumStatus.Enable.getCode());
        java.util.List<AppChargeAccount> list = appChargeAccountMapper.selectByExample(example);

        Map<String, Integer> typeAccountId = new HashMap<>();

        for (AppChargeAccount account : list) {
            typeAccountId.put(account.getPayType(), account.getAccountId());
        }

        for (EnumPayType payType : EnumPayType.values()) {
            if (!typeAccountId.containsKey(payType.getCode())) {
                typeAccountId.put(payType.getCode(), null);
            }
        }
        return typeAccountId;
    }

    /**
     * 应用支付方式授权
     *
     * @param appAccountIds
     * @return
     */
    @Override
    public boolean grantPay(AppAccountIds appAccountIds) {
        //支付方式添加 ，这里还需要加
        Map<Integer, TModel> pageModelMap = warpAccountIds(appAccountIds);
        //页面上选的accountIds
        List<Integer> pageChoose = pageModelMap.values().stream().map(m -> m.getAccId()).collect(Collectors.toList());
        //已设置的应用收款账号关系
        List<AppChargeAccount> AppChargeAccountReList = chargeConfigService.getRByAppId(appAccountIds.getAppKey());
        //页面啥都没传
        if (pageModelMap == null || pageModelMap.size() == 0) {
            deleteByappIdAccId(appAccountIds.getAppKey(), pageChoose);
            return true;
        }
        //以前无数据
        if (AppChargeAccountReList.isEmpty()) {
            //直接添加关系；
            addAppConfigRelation(appAccountIds.getAppKey(),
                    pageModelMap.values().stream().collect(Collectors.toList()));
            return true;
        } else {
            //已配置的支付方式ID
            List<Integer> hasPay =
                    AppChargeAccountReList.stream().map(e -> e.getAccountId()).collect(Collectors.toList());
            //页面数据-数据库已有数据就是需要增加的
            List<Integer> watiAddList = pageChoose;
            watiAddList.removeAll(hasPay);
            //数据库有的，页面没有的 就是要删除的
            List<Integer> waitDelete = hasPay;
            waitDelete.removeAll(pageChoose);
            //添加
            List<TModel> waitAddModel = watiAddList.stream().map(e -> pageModelMap.get(e)).collect(Collectors.toList());
            addAppConfigRelation(appAccountIds.getAppKey(), waitAddModel);
            //移除
            deleteByappIdAccId(appAccountIds.getAppKey(), waitDelete);
            return true;
        }
    }


    int addAppConfigRelation(String appKey, List<TModel> reList) {
        log.info("添加" + reList.toString());
        return 0;
    }

    int deleteByappIdAccId(String appKey, List<Integer> accIds) {
        log.info("删除" + accIds.toString());
        return 0;
    }

    private Map<Integer, TModel> warpAccountIds(AppAccountIds appAccountIds) {
        Map<Integer, TModel> pageModelMap = new HashMap();
        if (appAccountIds.getAlipayH5() != 0) {
            pageModelMap.put(appAccountIds.getAlipayH5(), new TModel(EnumPayType.AlipayH5.getCode(),
                    appAccountIds.getAlipayH5()));
        }
        if (appAccountIds.getAlipaySDK() != 0) {
            pageModelMap.put(appAccountIds.getAlipaySDK(), new TModel(EnumPayType.AlipaySDK.getCode(),
                    appAccountIds.getAlipaySDK()));
        }
        if (appAccountIds.getWechatH5() != 0) {
            pageModelMap.put(appAccountIds.getWechatH5(), new TModel(EnumPayType.WechatH5.getCode(),
                    appAccountIds.getWechatH5()));
        }
        if (appAccountIds.getWechatJSAPI() != 0) {
            pageModelMap.put(appAccountIds.getWechatJSAPI(), new TModel(EnumPayType.WechatJSAPI.getCode(),
                    appAccountIds.getWechatJSAPI()));
        }
        if (appAccountIds.getWechatNative() != 0) {
            pageModelMap.put(appAccountIds.getWechatNative(), new TModel(EnumPayType.WechatNAtive.getCode(),
                    appAccountIds.getWechatNative()));
        }
        if (appAccountIds.getWechatSDK() != 0) {
            pageModelMap.put(appAccountIds.getWechatSDK(), new TModel(EnumPayType.WechatSDK.getCode(),
                    appAccountIds.getWechatSDK()));
        }

        return pageModelMap;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    private class TModel {
        String payTypeCode;
        int accId;
    }

}
