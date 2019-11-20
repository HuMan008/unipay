package cn.gotoil.unipay.web.services;

import cn.gotoil.bill.web.services.AdminUserService;
import cn.gotoil.unipay.model.entity.AdminUser;

/**
 * @author think <syj247@qq.com>、
 * @date 2019-11-19、17:58
 */
public interface MyAdminUserService extends AdminUserService {
    /**
     *  登录
     */
    Object doLogin(String loginCode, String pwd);

    /**
     * code 加载对象
     * @param loginCode
     * @return
     */
    AdminUser loadByCode(String loginCode);
}
