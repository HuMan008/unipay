package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.bill.web.services.AdminUserService;
import cn.gotoil.unipay.config.properties.UserConfig;
import cn.gotoil.unipay.config.properties.UserDefine;
import cn.gotoil.unipay.exceptions.UnipayError;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("web/user")
@Api(description = "用户管理")
public class unipayController {

    @Autowired
    AdminUserService adminUserService;

    @Autowired
    UserConfig userConfig;

    @ApiOperation(value = "登录接口", position = 5, tags = "用户")
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @NeedLogin(value = false)
    public Object loginAction(@ApiParam(value = "用户名") @RequestParam String code,
                              @ApiParam(value = "密码") @RequestParam String pwd, HttpServletRequest request,
                              HttpServletResponse response) {
        UserDefine dd = userConfig.getUsers().get(code);
        if (null == dd) {
            throw new BillException(UnipayError.WebUserError_UserPwdError);
        }
        if (dd.getPwd().equals(pwd)) {
            UserDefine.fill(dd);
            String token = adminUserService.afterLogin(dd, code, pwd);
            dd.setToken(token);
            return new BillApiResponse(dd);
        } else {
            throw new BillException(UnipayError.WebUserError_UserPwdError);

        }
    }
}
