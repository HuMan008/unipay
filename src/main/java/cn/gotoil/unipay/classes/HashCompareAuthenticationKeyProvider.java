package cn.gotoil.unipay.classes;


import cn.gotoil.unipay.UnipayApplication;
import cn.gotoil.unipay.web.services.AppService;

/**
 * App
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、10:45
 */
public class HashCompareAuthenticationKeyProvider {

    @SuppressWarnings("all")
    public static String key(String appKey) {
        AppService appService = UnipayApplication.getApplicationContext().getBean(AppService.class);
        return appService.key(appKey);
    }
}
