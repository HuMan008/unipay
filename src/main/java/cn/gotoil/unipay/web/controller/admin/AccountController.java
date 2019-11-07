package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.web.message.request.admin.AccountAddRequest;
import cn.gotoil.unipay.web.services.ChargeConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("web/account")
@Api(description = "账号管理")
public class AccountController {

    @Autowired
    ChargeConfigService configService;

    @ApiOperation(value = "新增账号", position = 1, tags = "账号管理")
    @RequestMapping(value = "/addAccount", method = {RequestMethod.POST})
    @NeedLogin
    public Object addAccountAction(@RequestBody AccountAddRequest accountAddRequest) {
        if (!configService.checkName(accountAddRequest.getName(), null)) {
            throw new BillException(5000, "名称重复");
        }
        ChargeConfig chargeConfig = new ChargeConfig();
        BeanUtils.copyProperties(accountAddRequest, chargeConfig);
        chargeConfig.setStatus(accountAddRequest.getStatus().byteValue());
        if (configService.addChargeConfig(chargeConfig) != 1) {
            throw new BillException(5000, "新增收款账号失败");
        }
        return new BillApiResponse("新增成功");
    }

    @ApiOperation(value = "检查账号名称是否重复", position = 3, tags = "账号管理")
    @RequestMapping(value = "/checkAccountName", method = {RequestMethod.GET})
    @NeedLogin
    public Object checkAccountNameAction(@ApiParam(value = "名称") @RequestParam String appName,
                                         @ApiParam(value = "id") @RequestParam(required = false) Integer id) {
        return configService.checkName(appName, id);
    }

    @ApiOperation(value = "修改状态", position = 5, tags = "账号管理")
    @RequestMapping(value = "/updateAccountStatus", method = {RequestMethod.POST})
    @NeedLogin
    public Object updateAccountStatusAction(@ApiParam(value = "id") @RequestParam Integer id, @ApiParam(value = "新状态 0启用 " +
            "1禁用", allowableValues = "0,1", example = "0") @RequestParam Integer status) {
        return configService.updateStatus(id, status.byteValue());
    }

    @ApiOperation(value = "获取账号", position = 7, tags = "账号管理")
    @RequestMapping(value = "/getAccountById", method = {RequestMethod.GET})
    @NeedLogin
    public Object getAccountByIdAction(@ApiParam(value = "id") @RequestParam Integer id) {
        return new BillApiResponse(configService.loadByChargeId(id));
    }

    @ApiOperation(value = "修改账号", position = 11, tags = "账号管理")
    @RequestMapping(value = "/updateAccount", method = {RequestMethod.POST})
    @NeedLogin
    public Object updateAccountAction(@RequestBody AccountAddRequest accountAddRequest) {
        if (!configService.checkName(accountAddRequest.getName(), accountAddRequest.getId())) {
            throw new BillException(5000, "名称重复");
        }
        ChargeConfig chargeConfig = new ChargeConfig();
        BeanUtils.copyProperties(accountAddRequest, chargeConfig);
        if (configService.updateAccount(chargeConfig) != 1) {
            throw new BillException(5000, "修改收款账号失败");
        }
        return new BillApiResponse("修改成功");
    }

    @ApiOperation(value = "账号列表", position = 13, tags = "账号管理")
    @RequestMapping(value = "/queryAccounts", method = {RequestMethod.GET})
    @NeedLogin
    public Object queryAccountsAction(@ApiParam(value = "name") @RequestParam(required = false) String name,
                                      @ApiParam(value = "payType") @RequestParam(required = false) String payType,
                                      @ApiParam(value = "status") @RequestParam(required = false) String status) {

        return new BillApiResponse(configService.queryAccounts(name, payType, status));
    }

    @ApiOperation(value = "刷新账号列表", position = 15, tags = "账号管理")
    @RequestMapping(value = "/refreshAccount", method = {RequestMethod.GET})
    @NeedLogin
    public Object refreshAccountAction() {
        if (configService.refreshAccount() != 1) {
            throw new BillException(5000, "刷新失败");
        }
        return new BillApiResponse("刷新成功");
    }



}
