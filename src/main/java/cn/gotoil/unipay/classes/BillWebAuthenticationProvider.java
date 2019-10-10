package cn.gotoil.unipay.classes;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.model.BaseAdminUser;
import cn.gotoil.unipay.UnipayApplication;
import cn.gotoil.unipay.config.properties.UserConfig;
import cn.gotoil.unipay.config.properties.UserDefine;
import cn.gotoil.unipay.exceptions.UnipayError;

/**
 * 访问权限
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-20、10:46
 */
public class BillWebAuthenticationProvider {

    public static BaseAdminUser findByUserCode(String userCode) {

        UserConfig userHelper = UnipayApplication.getApplicationContext().getBean(UserConfig.class);
        UserDefine dd = userHelper.getUsers().get(userCode);
        if (dd == null) {
            throw new BillException(UnipayError.WebUserError_UserPwdError);
        }
        UserDefine.fill(dd);

        return dd;
    }
}
