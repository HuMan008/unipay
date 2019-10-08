package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.AppAccountIds;
import cn.gotoil.unipay.model.enums.EnumPayCategory;
import cn.gotoil.unipay.web.message.request.AppAddReuqest;
import cn.gotoil.unipay.web.message.request.AppListRequest;
import cn.gotoil.unipay.web.services.AppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("web/app")
@Api(description = "应用管理api")
public class AppController {

    @Autowired
    AppService appService;

    @ResponseBody
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

    @ResponseBody
    @ApiOperation(value = "收款账号类型", position = 3, tags = "应用管理")
    @RequestMapping(value = "getAccounts", method = RequestMethod.GET)
    @NeedLogin
    public Object getAccountTypes(){
        return new BillApiResponse(EnumPayCategory.values());
    }

    @ResponseBody
    @ApiOperation(value = "新增APP", position = 5, tags = "应用管理")
    @RequestMapping(value = "/addApp", method = {RequestMethod.POST})
    @NeedLogin
    public Object addAccount(@RequestBody AppAddReuqest appAddReuqest,@RequestBody AppAccountIds appAccountIds){
        if(appService.nameHasExist(appAddReuqest.getAppName(),null)){
            throw new BillException(5000,"应用名称重复");
        }

        App app = new App();
        BeanUtils.copyProperties(appAddReuqest, app);
        if(appService.createApp(app,appAccountIds) != 1){
            throw new BillException(5000,"新增应用失败");
        }
        return new BillApiResponse("新增应用成功");
    }


    @ResponseBody
    @ApiOperation(value = "查询APP", position = 7, tags = "应用管理")
    @RequestMapping(value = "/queryApp", method = {RequestMethod.GET})
    @NeedLogin
    public Object queryApp(@Valid @RequestBody AppListRequest appListRequest){
        return appService.queryApps(appListRequest);
    }
}
