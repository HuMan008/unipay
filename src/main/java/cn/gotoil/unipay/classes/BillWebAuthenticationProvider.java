package cn.gotoil.unipay.classes;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.model.BaseAdminUser;
import cn.gotoil.unipay.UnipayApplication;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.web.services.MyAdminUserService;

/**
 * 访问权限
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、10:46
 */
public class BillWebAuthenticationProvider {

    public static BaseAdminUser findByUserCode(String userCode) {

        MyAdminUserService myAdminUserService = UnipayApplication.getApplicationContext().getBean(MyAdminUserService.class);
        BaseAdminUser dd = myAdminUserService.loadByCode(userCode);
        if (dd == null) {
            throw new BillException(UnipayError.WebUserError_UserPwdError);
        }
        return dd;
    }
}
