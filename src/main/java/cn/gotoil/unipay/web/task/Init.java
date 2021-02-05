package cn.gotoil.unipay.web.task;

import cn.gotoil.unipay.classes.PayDispatcher;
import cn.gotoil.unipay.model.ChargeAccount;
import cn.gotoil.unipay.model.ChargeAlipayModel;
import cn.gotoil.unipay.model.ChargeWechatV2Model;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.web.helper.AlipayConfigHelper;
import cn.gotoil.unipay.web.helper.WechatClientHelper;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/1/30.
 */
@Component
public class Init implements CommandLineRunner  {

    Logger logger = LoggerFactory.getLogger(Init.class);

    @Autowired
    ChargeConfigService chargeConfigService;
    @Autowired
    AppService appService;
    @Autowired
    PayDispatcher payDispatcher;


    //加载APP
    //加载收款装好
    //加载app与收款账号关系

    @Override
    public void run(String... args) throws Exception {
        logger.info("缓存收款账号");
        chargeConfigService.refreshAccount();
        logger.info("缓存应用信息");
        appService.refreshApp();
        logger.info("缓存应用于收款账号关系");
        appService.refreshAppChargeAccountRedis();

        logger.info("=====>\t加载微信支付商户client");
        List<ChargeConfig> configList = chargeConfigService.queryAccounts("", "", "0");
        Set<String> initedMerSet = new HashSet<>();
        for (ChargeConfig chargeConfig : configList) {
            ChargeAccount account = payDispatcher.getChargeAccountBean(chargeConfig, "v2");
            if (account instanceof ChargeWechatV2Model) {
                String mid = ((ChargeWechatV2Model) account).getMerchId();
                if (!initedMerSet.contains(mid)) {
                    WechatClientHelper.getInstance().initClientMap((ChargeWechatV2Model) account);
                    initedMerSet.add(mid);
                }
            } else if (account instanceof ChargeAlipayModel) {
                AlipayConfigHelper.getInstance().init((ChargeAlipayModel) account);
            }
        }
        logger.info("=====>\t结束");


    }




}
