package cn.gotoil.unipay.web.services.impl;


import cn.gotoil.bill.exception.BillException;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 应用服务实现
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、10:48
 */
@Service
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
    public int createApp(App app, AppAccountIds appAccountIds) {
        app.setAppSecret(RandomStringUtils.random(40, true, true));
        app.setAppKey(RandomStringUtils.random(32, true, true));
        app.setStatus(EnumStatus.Enable.getCode());
        Date d = new Date();
        app.setCreatedAt(d);
        app.setUpdatedAt(d);
        redisHashHelper.set(APPKEY + app.getAppKey(), app, IGNORESET);
        appChargeAccountMapper(app.getAppKey(), appAccountIds);
        return appMapper.insert(app);
    }

    private void appChargeAccountMapper(String appId, AppAccountIds appAccountIds) {
        if (appAccountIds != null) {
            created(appId, appAccountIds.getAlipaySDKId(), EnumPayType.AlipaySDK.getCode());
            created(appId, appAccountIds.getAlipayH5Id(), EnumPayType.AlipayH5.getCode());
            created(appId, appAccountIds.getWechatJSAPIId(), EnumPayType.WechatJSAPI.getCode());
            created(appId, appAccountIds.getWechatSDKId(), EnumPayType.WechatSDK.getCode());
            created(appId, appAccountIds.getWechatNAtiveId(), EnumPayType.WechatNAtive.getCode());
            created(appId, appAccountIds.getWechatH5Id(), EnumPayType.WechatH5.getCode());
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
        return appMapper.updateByPrimaryKeySelective(updateApp) == 1 ? true : false;
    }


    /**
     * 更新APP
     *
     * @param app
     * @param appAccountIds
     * @return
     */
    @Override
    @OpLog(desc = "更新应用")
    public int updateApp(App app, AppAccountIds appAccountIds) {
        int result = appMapper.updateByPrimaryKeySelective(app);

        //禁用类型
        List<String> disable = new ArrayList<String>();

        setAccount(disable, appAccountIds.getAlipaySDKId(), EnumPayType.AlipaySDK.getCode(), app.getAppKey());
        setAccount(disable, appAccountIds.getAlipayH5Id(), EnumPayType.AlipayH5.getCode(), app.getAppKey());
        setAccount(disable, appAccountIds.getWechatJSAPIId(), EnumPayType.WechatJSAPI.getCode(), app.getAppKey());
        setAccount(disable, appAccountIds.getWechatSDKId(), EnumPayType.WechatSDK.getCode(), app.getAppKey());
        setAccount(disable, appAccountIds.getWechatH5Id(), EnumPayType.WechatH5.getCode(), app.getAppKey());
        setAccount(disable, appAccountIds.getWechatNAtiveId(), EnumPayType.WechatNAtive.getCode(), app.getAppKey());


        if (disable.size() > 0) {
            //禁用关联支付帐号信息
            HashMap param = new HashMap();
            param.put("appkey", app.getAppKey());
            param.put("pays", disable);
            param.put("status", EnumStatus.Disable.getCode());

            appQueryMapper.updateChargeaccountStatusByType(param);

            List<AppChargeAccount> acs = appQueryMapper.selectChargeaccountStatusByType(param);
            for (AppChargeAccount ac : acs) {
                chargeConfigService.addAppChargeAccount2Redis(ac);
            }
        }
        if (result == 1) {
            App appNew = appMapper.selectByPrimaryKey(app.getAppKey());
            redisHashHelper.set(APPKEY + app.getAppKey(), appNew, IGNORESET);
            for (String t : disable) {
                removeAppChargeAccountFromRedis(t, app.getAppKey());
            }
        }
        return result;
    }

    private void removeAppChargeAccountFromRedis(String payType, String appId) {
        String key = ChargeConfigServiceImpl.APPCHARGKEY + payType + "_" + appId;
        redisTemplate.opsForHash().getOperations().expire(key, 0, TimeUnit.SECONDS);
    }

    private void setAccount(List<String> disable, Integer accountId, String type, String appId) {
        if (accountId == null || "".equals(accountId)) {
            disable.add(type);
        } else {//启用或新增关联支付帐号信息
            AppChargeAccountExample example = new AppChargeAccountExample();
            example.createCriteria().andAppIdEqualTo(appId)
                    .andPayTypeEqualTo(type)
                    .andAccountIdEqualTo(accountId);
            List<AppChargeAccount> chares = appChargeAccountMapper.selectByExample(example);

            if (chares.size() > 0) {
                setCharge(chares.get(0));
            } else {
                createAndSetStatus(appId, accountId, type);
            }
        }
    }

    private void setCharge(AppChargeAccount appChargeAccount) {
        if (appChargeAccount != null && appChargeAccount.getStatus() != EnumStatus.Enable.getCode()) {
            AppChargeAccount updateAC = new AppChargeAccount();
            updateAC.setId(appChargeAccount.getId());
            updateAC.setUpdatedAt(new Date());
            updateAC.setStatus(EnumStatus.Enable.getCode());
            appChargeAccountMapper.updateByPrimaryKeySelective(updateAC);
            updateAC = appChargeAccountMapper.selectByPrimaryKey(updateAC.getId());
//            redisHashHelper.set(APPCHARGKEY+updateAC.getId(),updateAC,redisExceptFieldsForApp);

            Map param = new HashMap<>();
            param.put("status", EnumStatus.Disable.getCode());
            param.put("appid", appChargeAccount.getAppId());
            param.put("payType", appChargeAccount.getPayType());
            param.put("accid", appChargeAccount.getAccountId());
            appQueryMapper.updateChargeaccountStatusById(param);

            List<AppChargeAccount> acs = appQueryMapper.selectChargeaccountStatusById(param);
            for (AppChargeAccount ac : acs) {
//                redisHashHelper.set(APPCHARGKEY+ac.getId(),ac,redisExceptFieldsForApp);
                chargeConfigService.addAppChargeAccount2Redis(ac);
            }
        }
    }

    private void createAndSetStatus(String appid, Integer accid, String type) {
        created(appid, accid, type);

        Map param = new HashMap<>();
        param.put("status", EnumStatus.Disable.getCode());
        param.put("appid", appid);
        param.put("payType", type);
        param.put("accid", accid);
        appQueryMapper.updateChargeaccountStatusById(param);
    }

    /**
     * 查询有效APP
     *
     * @return
     */
    @Override
    public List getApps() {
        AppExample example = new AppExample();
        example.createCriteria().andStatusEqualTo(EnumStatus.Enable.getCode());
        return appMapper.selectByExample(example);
    }

    /**
     * 根据APPKEY获取配置的收款账号
     *
     * @param appkey
     * @return
     */
    @Override
    public AppAccountIds getByAppkey(String appkey) {

        AppChargeAccountExample example = new AppChargeAccountExample();
        example.createCriteria().andAppIdEqualTo(appkey).andStatusEqualTo(EnumStatus.Enable.getCode());
        java.util.List<AppChargeAccount> list = appChargeAccountMapper.selectByExample(example);

        Map<String, Integer> typeAccountId = new HashMap<>();
        for (AppChargeAccount account : list) {
            typeAccountId.put(account.getPayType(), account.getAccountId());
        }

        AppAccountIds appAccountIds = new AppAccountIds();
        if (typeAccountId.containsKey(EnumPayType.AlipayH5.getCode())) {
            appAccountIds.setAlipayH5Id(typeAccountId.get(EnumPayType.AlipayH5.getCode()));
        }

        if (typeAccountId.containsKey(EnumPayType.AlipaySDK.getCode())) {
            appAccountIds.setAlipayH5Id(typeAccountId.get(EnumPayType.AlipaySDK.getCode()));
        }

        if (typeAccountId.containsKey(EnumPayType.WechatNAtive.getCode())) {
            appAccountIds.setAlipayH5Id(typeAccountId.get(EnumPayType.WechatNAtive.getCode()));
        }

        if (typeAccountId.containsKey(EnumPayType.WechatH5.getCode())) {
            appAccountIds.setAlipayH5Id(typeAccountId.get(EnumPayType.WechatH5.getCode()));
        }

        if (typeAccountId.containsKey(EnumPayType.WechatJSAPI.getCode())) {
            appAccountIds.setAlipayH5Id(typeAccountId.get(EnumPayType.WechatJSAPI.getCode()));
        }

        if (typeAccountId.containsKey(EnumPayType.WechatSDK.getCode())) {
            appAccountIds.setAlipayH5Id(typeAccountId.get(EnumPayType.WechatSDK.getCode()));
        }
        return appAccountIds;
    }
}
