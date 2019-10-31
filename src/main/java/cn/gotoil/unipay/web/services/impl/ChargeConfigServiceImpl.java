package cn.gotoil.unipay.web.services.impl;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.helper.RedisHashHelper;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.AppChargeAccount;
import cn.gotoil.unipay.model.entity.AppChargeAccountExample;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.ChargeConfigExample;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.mapper.AppChargeAccountMapper;
import cn.gotoil.unipay.model.mapper.ChargeConfigMapper;
import cn.gotoil.unipay.model.mapper.ext.ExtChargeConfigMapper;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.web.annotation.OpLog;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 应用收款账号配置及关系实现类
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、15:54
 */
@Service
@Slf4j
public class ChargeConfigServiceImpl implements ChargeConfigService {

    public static final String APPCHARGKEY = "Unipay:appCharge:";

    static final Set<String> IGNORESET = Sets.newHashSet("createdAt", "updatedAt");

    @Resource
    RedisHashHelper redisHashHelper;
    @Resource
    AppChargeAccountMapper appChargeAccountMapper;
    @Resource
    ChargeConfigMapper chargeConfigMapper;
    @Resource
    ExtChargeConfigMapper extChargeConfigMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;


    /**
     * config对象
     *
     * @param appChargeAccountId
     * @return
     */
    public String appChargAccountKey(int appChargeAccountId) {
        return APPCHARGKEY + appChargeAccountId;
    }

    /**
     * 关系
     *
     * @param appId
     * @param payTypeCode
     * @return
     */
    public String appChargAccountKey4AppidAndPayType(String appId, String payTypeCode) {
        return APPCHARGKEY + payTypeCode + "_" + appId;
    }

    /**
     * 根据条件查询收款账号
     *
     * @param accountName
     * @param payType
     * @param status
     * @return
     */
    @Override
    public List queryAccounts(String accountName, String payType, String status) {
        ChargeConfigExample example = new ChargeConfigExample();
        ChargeConfigExample.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotEmpty(accountName)) {
            criteria.andNameLike("%" + accountName);
        }
        if (StringUtils.isNotEmpty(payType)) {
            criteria.andPayTypeEqualTo(payType);
        }
        if (StringUtils.isNotEmpty(status)) {
            criteria.andStatusEqualTo(Byte.valueOf(status));
        }

        example.setOrderByClause("created_at desc");

        return chargeConfigMapper.selectByExampleWithBLOBs(example);
    }

    @Override
    public void addAppChargeAccount2Redis(AppChargeAccount appChargeAccount) {
//        String key1 = appChargAccountKey(appChargeAccount.getId());
        String key2 = appChargAccountKey4AppidAndPayType(appChargeAccount.getAppId(), appChargeAccount.getPayType());
//        redisHashHelper.set(key1, appChargeAccount, IGNORESET);
        redisHashHelper.set(key2, appChargeAccount, IGNORESET);
    }



    /**
     * 根据appid,收款类型，找具体的收款账号对象
     *
     * @param appId
     * @param payType
     * @return
     */
    @Override
    public ChargeConfig loadByAppIdPayType(String appId, String payType) {
        String key = appChargAccountKey4AppidAndPayType(appId, payType);
        AppChargeAccount appChargeAccount = redisHashHelper.get(key, AppChargeAccount.class);
        if (appChargeAccount != null) {
            return loadByChargeId(appChargeAccount.getAccountId());
        } else {
            AppChargeAccountExample appChargeAccountExample = new AppChargeAccountExample();
            appChargeAccountExample.createCriteria().andPayTypeEqualTo(payType).andAppIdEqualTo(appId);
            List<AppChargeAccount> appChargeAccountList =
                    appChargeAccountMapper.selectByExample(appChargeAccountExample);
            if (1 == appChargeAccountList.size()) {
                appChargeAccount = appChargeAccountList.get(0);
                redisHashHelper.set(key, appChargeAccount, IGNORESET);
                int accountId = appChargeAccount.getAccountId();
                return loadByChargeId(accountId);
            } else {
                return null;
            }
        }



    }

    /**
     * 根据账号ID加载对象
     *
     * @param configId
     * @return
     */
    @Override
    public ChargeConfig loadByChargeId(int configId) {
        String key = appChargAccountKey(configId);
        ChargeConfig chargeConfig = redisHashHelper.get(key, ChargeConfig.class);
        if (chargeConfig == null) {
            chargeConfig = chargeConfigMapper.selectByPrimaryKey(configId);
            if (chargeConfig != null) {
                redisHashHelper.set(key, chargeConfig, IGNORESET);
                ;
            }
            return chargeConfig;
        } else {
            return chargeConfig;
        }
    }

    private boolean checkConfig(ChargeConfig chargeConfig) {
        if (EnumPayType.WechatH5.getCode().equals(chargeConfig.getPayType())
                || EnumPayType.WechatJSAPI.getCode().equals(chargeConfig.getPayType())
                || EnumPayType.WechatNAtive.getCode().equals(chargeConfig.getPayType())
                || EnumPayType.WechatSDK.getCode().equals(chargeConfig.getPayType())) {
            JSONObject jsonObject = JSONObject.parseObject(chargeConfig.getConfigJson());
            ChargeWechatModel wechatModel = JSON.toJavaObject(jsonObject, ChargeWechatModel.class);
            return UtilString.checkObjFieldIsNull(wechatModel);

        } else if (EnumPayType.AlipayH5.getCode().equals(chargeConfig.getPayType())
                || EnumPayType.AlipaySDK.getCode().equals(chargeConfig.getPayType())) {
            JSONObject jsonObject = JSONObject.parseObject(chargeConfig.getConfigJson());
            ChargeAlipayModel alipayModel = JSON.toJavaObject(jsonObject, ChargeAlipayModel.class);
            return UtilString.checkObjFieldIsNull(alipayModel);


        } else {
            throw new BillException(9100, "没有对应支付类型");
        }
    }

    /**
     * 新增收款账号
     *
     * @param chargeConfig
     * @return
     */
    @Override
    @OpLog(desc = "新增收款账号")
    public int addChargeConfig(ChargeConfig chargeConfig) {

        boolean flag = checkConfig(chargeConfig);

        if (flag) {
            throw new BillException(9100, "收款信息配置有误");
        }
        chargeConfig.setCreatedAt(new Date());
        chargeConfig.setUpdatedAt(new Date());
        int result = extChargeConfigMapper.insertChargeConfig(chargeConfig);
        if (result == 1) {
            String key = appChargAccountKey(chargeConfig.getId());
            redisHashHelper.set(key, chargeConfig, Sets.newHashSet("config"));
        }
        return result;
    }

    /**
     * 更新收款账号
     *
     * @param chargeConfig
     * @return
     */
    @Override
    @OpLog(desc = "修改收款账号")
    public int updateAccount(ChargeConfig chargeConfig) {
        boolean flag = checkConfig(chargeConfig);

        if (flag) {
            throw new BillException(9100, "收款信息配置有误");
        }

        Date now = new Date();
        chargeConfig.setUpdatedAt(now);
        int result = chargeConfigMapper.updateByPrimaryKeyWithBLOBs(chargeConfig);
        if (result == 1) {
            String key = appChargAccountKey(chargeConfig.getId());
            redisHashHelper.set(key, chargeConfig, Sets.newHashSet("config"));
        }
        return result;
    }

    /**
     * 检查名字是否重复
     *
     * @param name
     * @param id
     * @return
     */
    @Override
    public boolean checkName(String name, Integer id) {
        ChargeConfigExample example = new ChargeConfigExample();
        ChargeConfigExample.Criteria criteria = example.createCriteria();
        if (id != null) {
            criteria.andIdNotEqualTo(id);
        }
        criteria.andNameEqualTo(name);

        List list = chargeConfigMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 修改状态
     *
     * @param id
     * @param status
     * @return
     */
    @Override
    @OpLog(desc = "更新收款账号状态")
    public boolean updateStatus(Integer id, Byte status) {
        ChargeConfig old = loadByChargeId(id);
        if (old == null) {
            throw new BillException(9100, "账号不存在");
        }

        ChargeConfig updateConfig = new ChargeConfig();
        updateConfig.setId(id);
        updateConfig.setStatus(status);
        updateConfig.setUpdatedAt(new Date());
        int result = chargeConfigMapper.updateByPrimaryKeySelective(updateConfig);
        if (result == 1) {
            ChargeConfig c1 = chargeConfigMapper.selectByPrimaryKey(id);
            String key = appChargAccountKey(c1.getId());
            redisHashHelper.set(key, c1, Sets.newHashSet("config"));
        }
        return result == 1 ? true : false;
    }

    /**
     * 刷新账号配置
     *
     * @return
     */
    @Override
    public int refreshAccount() {
        try {
            ChargeConfigExample example = new ChargeConfigExample();
            List<ChargeConfig> list = chargeConfigMapper.selectByExample(example);
            for (ChargeConfig config : list) {
                String key = appChargAccountKey(config.getId());
                redisHashHelper.set(key, config, IGNORESET);
            }
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0;
    }
}


