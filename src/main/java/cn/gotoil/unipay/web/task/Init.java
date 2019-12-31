package cn.gotoil.unipay.web.task;

import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
    }




}
