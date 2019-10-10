package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.AppAccountIds;
import cn.gotoil.unipay.model.enums.EnumPayCategory;
import cn.gotoil.unipay.model.enums.EnumStatus;
import cn.gotoil.unipay.web.message.request.admin.AppAddRquest;
import cn.gotoil.unipay.web.message.request.admin.AppListRequest;
import cn.gotoil.unipay.web.services.AppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("web/app")
@Api(description = "应用管理api")
public class AppController {

    @Autowired
    AppService appService;

    @ApiOperation(value = "收款账号", position = 1, tags = "应用管理")
    @RequestMapping(value = "getAccounts", method = RequestMethod.GET)
    @NeedLogin
    public Object getAccounts(){
        HashMap<String,List> result = new HashMap<>();
        for(EnumPayCategory enums : EnumPayCategory.values()){
            result.put(enums.getDescp(),appService.queryAllAccount(enums.getCode()));
        }
        return result;
    }

    @ApiOperation(value = "状态类型", position = 2, tags = "应用管理")
    @RequestMapping(value = "getStatus", method = RequestMethod.GET)
    @NeedLogin
    public Object getStatus(){
        return new BillApiResponse(EnumStatus.values());
    }

    @ApiOperation(value = "收款账号类型", position = 3, tags = "应用管理")
    @RequestMapping(value = "getAccountTypes", method = RequestMethod.GET)
    @NeedLogin
    public Object getAccountTypes(){
        return new BillApiResponse(EnumPayCategory.values());
    }

    @ApiOperation(value = "新增APP", position = 5, tags = "应用管理")
    @RequestMapping(value = "/addApp", method = {RequestMethod.POST})
    @NeedLogin
    public Object addAccount(@RequestBody AppAddRquest appAddRquest, @RequestBody AppAccountIds appAccountIds){
        if(appService.nameHasExist(appAddRquest.getAppName(),null)){
            throw new BillException(5000,"应用名称重复");
        }

        App app = new App();
        BeanUtils.copyProperties(appAddRquest, app);
        if(appService.createApp(app,appAccountIds) != 1){
            throw new BillException(5000,"新增应用失败");
        }
        return new BillApiResponse("新增应用成功");
    }


    @ApiOperation(value = "查询APP", position = 7, tags = "应用管理")
    @RequestMapping(value = "/queryApp", method = {RequestMethod.GET})
    @NeedLogin
    public Object queryApp(@Valid @RequestBody AppListRequest appListRequest){
        return appService.queryApps(appListRequest);
    }

    @ApiOperation(value = "修改APP状态", position = 9, tags = "应用管理")
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET})
    @NeedLogin
    public Object updateStatus(@ApiParam(value = "APPKEY") @PathVariable String appkey,@ApiParam(value = "新状态 0启用 1禁用", allowableValues = "0,1", example = "0") @PathVariable Integer status){
        return appService.updateStatus(appkey,status.byteValue());
    }

    @ApiOperation(value = "检查APP名称是否重复", position = 11, tags = "应用管理")
    @RequestMapping(value = "/checkAppName", method = {RequestMethod.GET})
    @NeedLogin
    public Object checkAppName(@ApiParam(value = "名称") @PathVariable String appName,@ApiParam(value = "APPKEY") @PathVariable String appKey){
        return appService.nameHasExist(appName,appKey);
    }

    @ApiOperation(value = "根据APPKEY获取APP", position = 13, tags = "应用管理")
    @RequestMapping(value = "/getAppByAppKey", method = {RequestMethod.GET})
    @NeedLogin
    public Object getAppByAppKey(String appKey){
        return new BillApiResponse(appService.load(appKey));
    }

    @ApiOperation(value = "修改APP", position = 15, tags = "应用管理")
    @RequestMapping(value = "/updateApp", method = {RequestMethod.POST})
    @NeedLogin
    public Object updateApp(@RequestBody AppAddRquest appAddRquest, @RequestBody AppAccountIds appAccountIds, HttpServletRequest request){
        if(appService.nameHasExist(appAddRquest.getAppName(), appAddRquest.getAppKey())){
            throw new BillException(5000,"应用名称重复");
        }

        App app = new App();
        BeanUtils.copyProperties(appAddRquest, app);
        if(appService.updateApp(app,appAccountIds) != 1){
            throw new BillException(5000,"修改应用失败");
        }
        return new BillApiResponse("修改应用成功");
    }

    @ApiOperation(value = "获取有效APP", position = 17, tags = "应用管理")
    @RequestMapping(value = "/getApps", method = {RequestMethod.GET})
    @NeedLogin
    public Object getApps(){
        return new BillApiResponse(appService.getApps());
    }
}
