package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.bill.web.services.impl.AdminUserServiceImpl;
import cn.gotoil.unipay.config.properties.UserDefine;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.entity.AdminUser;
import cn.gotoil.unipay.model.mapper.AdminUserMapper;
import cn.gotoil.unipay.web.services.MyAdminUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户实现
 *
 * @author think <syj247@qq.com>、
 * @date 2019-11-19、17:57
 */
@Service
public class MyAdminUserServiceImpl extends AdminUserServiceImpl implements MyAdminUserService {


    @Resource
    AdminUserMapper adminUserMapper;


    /**
     * 登录
     */
    @Override
    public Object doLogin(String loginCode, String pwd) {
        AdminUser adminUser = loadByCode(loginCode);
        if (null == adminUser) {
            throw new BillException(UnipayError.WebUserError_UserPwdError);
        }
        if (adminUser.getPwd().equals(pwd)) {
            UserDefine userDefine = UserDefine.warpAdminUser2Defind(adminUser);
            String token = afterLogin(userDefine, loginCode, userDefine.getUpwd());
            userDefine.setToken(token);
            return new BillApiResponse(userDefine);
        } else {
            throw new BillException(UnipayError.WebUserError_UserPwdError);

        }
    }

    /**
     * code 加载对象
     *
     * @param loginCode
     * @return
     */
    @Override
    public AdminUser loadByCode(String loginCode) {
        return adminUserMapper.selectByPrimaryKey(loginCode);
    }
}
