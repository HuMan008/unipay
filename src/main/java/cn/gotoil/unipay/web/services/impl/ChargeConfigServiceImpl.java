package cn.gotoil.unipay.web.services.impl;


import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.helper.RedisHashHelper;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatModel;
import cn.gotoil.unipay.model.entity.AppChargeAccount;
import cn.gotoil.unipay.model.entity.AppChargeAccountExample;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.ChargeConfigExample;
import cn.gotoil.unipay.model.enums.EnumPayCategory;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.mapper.AppChargeAccountMapper;
import cn.gotoil.unipay.model.mapper.ChargeConfigMapper;
import cn.gotoil.unipay.model.mapper.ext.ExtChargeConfigMapper;
import cn.gotoil.unipay.model.view.AccountView;
import cn.gotoil.unipay.utils.DateUtil;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
     * config对象 payCode_id
     *
     * @param id
     * @param payCode
     * @return
     */

    public String chargeConfigKey(int id, String payCode) {
        return APPCHARGKEY + payCode + "_" + id;
    }


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
        Set<String> keys = stringRedisTemplate.keys(APPCHARGKEY + "*");
        List<Map<String, Object>> list = new ArrayList<>();
        ArrayList<AccountView> avs = new ArrayList<>();
        for (String strkey : keys) {
            Map<String, Object> mm = redisHashHelper.get(strkey, Map.class);
            AccountView av = new AccountView();

            String name = String.valueOf(mm.get("name"));
            String type = strkey.split(":")[1];
            type = type.split("_")[0];
            String state = String.valueOf(mm.get("state"));

            if (StringUtils.isNotEmpty(accountName)
                    && !accountName.equals(name)) {
                continue;
            } else if (StringUtils.isNotEmpty(status)
                    && !status.equals(state)) {
                continue;
            } else if (StringUtils.isNotEmpty(payType)
                    && !payType.equals(type)) {
                continue;
            }


            av.setId(Integer.valueOf(String.valueOf(mm.get("id"))));
            av.setName(name);
            av.setState(Byte.valueOf(state));
            if (mm.get("createdAt") != null) {
                av.setCreatedAt(DateUtil.stringtoDateByNyrsfm(String.valueOf(mm.get("createdAt"))));
            }
            av.setUpdatedAt(DateUtil.stringtoDateByNyrsfm(String.valueOf(mm.get("updatedAt"))));
            av.setPayDesc(EnumPayCategory.getDescByCode(type));
            av.setPayType(type);
            avs.add(av);
        }
        Collections.sort(avs, new Comparator<AccountView>() {
            @Override
            public int compare(AccountView o1, AccountView o2) {
                if (o1.getPayType().hashCode() > o2.getPayType().hashCode()) {
                    return 1;
                } else if (o1.getPayType().hashCode() == o2.getPayType().hashCode()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        return avs;
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
    public int updateAccount(ChargeConfig chargeConfig) {
        boolean flag = checkConfig(chargeConfig);

        if (flag) {
            throw new BillException(9100, "收款信息配置有误");
        }

        Date now = new Date();
        chargeConfig.setUpdatedAt(now);
        int result = chargeConfigMapper.updateByPrimaryKey(chargeConfig);
        if (result == 1) {
            String key = chargeConfigKey(chargeConfig.getId(), chargeConfig.getPayType());
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
    public boolean updateStatus(Integer id, Byte status) {
        ChargeConfig old = loadByChargeId(id);
        if (old == null) {
            throw new BillException(9100, "账号不存在");
        }

        ChargeConfig updateConfig = new ChargeConfig();
        updateConfig.setId(id);
        updateConfig.setStatus(status);
        updateConfig.setUpdatedAt(new Date());
        return chargeConfigMapper.updateByPrimaryKeySelective(updateConfig) == 1 ? true : false;
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
                String key = chargeConfigKey(config.getId(), config.getPayType());
                redisHashHelper.set(key, config, Sets.newHashSet("config"));
            }
            return 1;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0;
    }
}


