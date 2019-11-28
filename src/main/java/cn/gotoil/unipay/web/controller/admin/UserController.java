package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.bill.web.services.AdminUserService;
import cn.gotoil.unipay.config.properties.UserConfig;
import cn.gotoil.unipay.config.properties.UserDefine;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.entity.AdminUser;
import cn.gotoil.unipay.model.entity.AdminUserExample;
import cn.gotoil.unipay.web.message.request.admin.ModifyPwdRequest;
import cn.gotoil.unipay.web.services.MyAdminUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("web/user")
@Api(description = "用户管理")
public class UserController {

    @Autowired
    MyAdminUserService adminUserService;

    @ApiOperation(value = "登录接口", position = 5, tags = "用户")
    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @NeedLogin(value = false)
    public Object loginAction(@ApiParam(value = "用户名") @RequestParam String code,
                              @ApiParam(value = "密码") @RequestParam String pwd, HttpServletRequest request,
                              HttpServletResponse response) {
       return adminUserService.doLogin(code,pwd);
    }


    @ApiOperation(value = "修改密码", position = 5, tags = "用户")
    @RequestMapping(value = "/uppwd", method = {RequestMethod.POST})
    @NeedLogin(value = true)
    public Object upPwdAction(@Valid @RequestBody ModifyPwdRequest modifyPwdRequest, HttpServletRequest request,
                              HttpServletResponse response) {
        return adminUserService.uppwd(request,modifyPwdRequest.getOldPwd(),modifyPwdRequest.getNewPwd());
    }

}
