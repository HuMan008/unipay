package cn.gotoil.unipay.web.task;

import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 基础数据刷新
 *
 * @author think <syj247@qq.com>、
 * @date 2019-12-31、10:23
 */
@Component
@Slf4j
public class BaseDataRefreshTask {

    @Autowired
    ChargeConfigService chargeConfigService;
    @Autowired
    AppService appService;


    @Scheduled(initialDelay = 1000 * 60 * 21, fixedDelay = 1000 * 60 * 30)
    public void appRefresh() {
        log.info("定时刷新app");
        try {
            appService.refreshApp();
        } catch (Exception e) {
            log.error("定时刷新app出错{}", e);
        }
    }

    @Scheduled(initialDelay = 1000 * 60 * 22, fixedDelay = 1000 * 60 * 30)
    public void chargeRefresh() {
        log.info("定时刷新收款账号");
        try {
            chargeConfigService.refreshAccount();
        } catch (Exception e) {
            log.error("定时刷新收款账号出错{}", e);
        }
    }

    @Scheduled(initialDelay = 1000 * 60 * 23, fixedDelay = 1000 * 60 * 30)
    public void appchargeRefresh() {
        log.info("定时刷新应用与收款账号关系");
        try {
            appService.refreshAppChargeAccountRedis();
        } catch (Exception e) {
            log.error("定时刷新应用与收款账号关系{}", e);
        }
    }


}
