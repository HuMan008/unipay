package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.exception.CommonError;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.bill.web.services.impl.AdminUserServiceImpl;
import cn.gotoil.unipay.config.properties.UserDefine;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.entity.AdminUser;
import cn.gotoil.unipay.model.mapper.AdminUserMapper;
import cn.gotoil.unipay.utils.UtilRequest;
import cn.gotoil.unipay.web.services.MyAdminUserService;
import com.auth0.jwt.JWT;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    @Override
    public Object uppwd(HttpServletRequest request, String oldPwd, String newPwd) {
        String token = request.getHeader("gtToken");
        String userId = JWT.decode(token).getClaim("id").asString();
        AdminUser adminUser = loadByCode(userId);
        if(adminUser==null){
            throw new BillException(CommonError.TokenError) ;
        }
        if(!adminUser.getPwd().equals(oldPwd)){
           throw new BillException(40003,"旧密码错误") ;
        }
        AdminUser newAdminUser = new AdminUser();
        newAdminUser.setPwd(newPwd);
        newAdminUser.setCode(adminUser.getCode());
        adminUserMapper.updateByPrimaryKeySelective(newAdminUser);
        return new BillApiResponse("密码修改成功，新密码为:【"+newPwd+"】");
    }
}
