package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.exception.BillException;
import cn.gotoil.bill.web.annotation.HasRole;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.config.consts.ConstsRole;
import cn.gotoil.unipay.exceptions.UnipayError;
import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.AppAccountIds;
import cn.gotoil.unipay.model.enums.EnumPayType;
import cn.gotoil.unipay.model.enums.EnumStatus;
import cn.gotoil.unipay.web.message.request.admin.AppAddRquest;
import cn.gotoil.unipay.web.message.request.admin.AppListRequest;
import cn.gotoil.unipay.web.message.response.admin.BaseComboResponse;
import cn.gotoil.unipay.web.message.response.admin.PayTypeWithCatResponse;
import cn.gotoil.unipay.web.message.response.admin.PaytypeResponse;
import cn.gotoil.unipay.web.services.AppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("web/app")
@Api(description = "应用管理")
public class AppController {

    @Autowired
    AppService appService;

    @ApiOperation(value = "收款账号", position = 1, tags = "应用管理")
    @RequestMapping(value = "getAccounts", method = RequestMethod.GET)
    @NeedLogin     @HasRole(value = ConstsRole.ADMIN)

    public Object getAccountsAction(@ApiParam(value = "appkey") @RequestParam String appkey) {
        ArrayList<PaytypeResponse> paytypeList = new ArrayList<>();
        Map<String,Integer> map = appService.getByAppkey(appkey);
        for (EnumPayType enums : EnumPayType.values()) {
            PaytypeResponse paytypeResponse = new PaytypeResponse();
            paytypeResponse.setPayName(enums.getDescp());
            paytypeResponse.setPayType(enums.getCode().substring(0,1).toLowerCase().concat(enums.getCode().substring(1)));
//            paytypeResponse.setPayType(enums.getCode());
            paytypeResponse.setChargeConfig(appService.queryAllAccount(enums.getCode()));
            paytypeResponse.setSelected(map.get(enums.getCode()));
            paytypeResponse.setPayCategoryType(enums.getEnumPayCategory().getCode());
            paytypeResponse.setPayCategoryTypeName(enums.getEnumPayCategory().getDescp());
            paytypeList.add(paytypeResponse);
        }

        Map<String, List<PaytypeResponse>> tempMap =
                paytypeList.stream().collect(Collectors.groupingBy(PaytypeResponse::getPayCategoryTypeName));
        List<PayTypeWithCatResponse> resultList = new ArrayList<>();
        for(Map.Entry<String, List<PaytypeResponse>> entity: tempMap.entrySet()){
          PayTypeWithCatResponse payTypeWithCatResponse = new PayTypeWithCatResponse(entity.getKey(),entity.getValue())  ;
            resultList.add(payTypeWithCatResponse);
        }
        return resultList;
    }

    @ApiOperation(value = "状态类型", position = 2, tags = "应用管理")
    @RequestMapping(value = "getStatus", method = RequestMethod.GET)
    @NeedLogin     @HasRole(value = ConstsRole.ADMIN)
    public Object getStatusAction() {
        return BaseComboResponse.getEnumsStatusCombo();
    }

    @ApiOperation(value = "收款账号类型", position = 3, tags = "应用管理")
    @RequestMapping(value = "getAccountTypes", method = RequestMethod.GET)
    @NeedLogin
    public Object getAccountTypesAction() {
        return  BaseComboResponse.getPayTypeCombo();
    }

    @ApiOperation(value = "支付方式类别类型", position = 3, tags = "应用管理")
    @RequestMapping(value = "getPayTypeCategoryCombo", method = RequestMethod.GET)
    @NeedLogin
    public Object getPayTypeCategoryComboAction() {
        return  BaseComboResponse.getPayTypeCategoryCodeCombo();
    }

    @ApiOperation(value = "新增APP", position = 5, tags = "应用管理")
    @RequestMapping(value = "/addApp", method = {RequestMethod.POST})
    @NeedLogin     @HasRole(value = ConstsRole.ADMIN)

    public Object addAccountAction(@RequestBody AppAddRquest appAddRquest) {
        if (appService.nameHasExist(appAddRquest.getAppName(), null)) {
            throw new BillException(5000, "应用名称重复");
        }
        App app = new App();
        BeanUtils.copyProperties(appAddRquest, app);
        if (appService.createApp(app) != 1) {
            throw new BillException(5000, "新增应用失败");
        }
        return new BillApiResponse("新增应用成功");
    }


    @ApiOperation(value = "查询APP", position = 7, tags = "应用管理")
    @RequestMapping(value = "/queryApp", method = {RequestMethod.POST})
    @NeedLogin     @HasRole(value = ConstsRole.ADMIN)
    public Object queryAppAction(@Valid @RequestBody AppListRequest appListRequest) {
        return appService.queryApps(appListRequest);
    }

    @ApiOperation(value = "修改APP状态", position = 9, tags = "应用管理")
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET})
    @NeedLogin    @HasRole(value = ConstsRole.ADMIN)

    public Object updateStatusAction(@ApiParam(value = "APPKEY") @RequestParam String appkey, @ApiParam(value = "新状态 0启用 " +
            "1禁用", allowableValues = "0,1", example = "0") @RequestParam Integer status) {
        return appService.updateStatus(appkey, status.byteValue());
    }

    @ApiOperation(value = "检查APP名称是否重复", position = 11, tags = "应用管理")
    @RequestMapping(value = "/checkAppName", method = {RequestMethod.GET})
    @NeedLogin     @HasRole(value = ConstsRole.ADMIN)

    public Object checkAppNameAction(@ApiParam(value = "名称") @RequestParam String appName,
                                     @ApiParam(value = "APPKEY") @RequestParam(required = false) String appKey) {
        return appService.nameHasExist(appName, appKey);
    }

    @ApiOperation(value = "根据APPKEY获取APP", position = 13, tags = "应用管理")
    @RequestMapping(value = "/getAppByAppKey", method = {RequestMethod.GET})
    @NeedLogin
    public Object getAppByAppKeyAction(@ApiParam(value = "appkey") @RequestParam String appKey) {
        return new BillApiResponse(appService.load(appKey));
    }

    @ApiOperation(value = "修改APP", position = 15, tags = "应用管理")
    @RequestMapping(value = "/updateApp", method = {RequestMethod.POST})
    @NeedLogin     @HasRole(value = ConstsRole.ADMIN)

    public Object updateAppAction(@RequestBody AppAddRquest appAddRquest, @RequestParam(required = false,
            defaultValue = "0") String checkName, HttpServletRequest request) {
        if ("0".equals(checkName)) {
            if (appService.nameHasExist(appAddRquest.getAppName(), appAddRquest.getAppKey())) {
                throw new BillException(5000, "应用名称重复");
            }
        }
        App app = new App();
        if (app.getStatus() == null) {
            app.setStatus(EnumStatus.Enable.getCode());
        }
        BeanUtils.copyProperties(appAddRquest, app);
        if (appService.updateApp(app) != 1) {
            throw new BillException(5000, "修改应用失败");
        }
        return new BillApiResponse("修改应用成功");
    }


    @ApiOperation(value = "应用支付方式授权", position = 15, tags = "应用管理")
    @RequestMapping(value = "/grantpay", method = {RequestMethod.POST})
    @NeedLogin     @HasRole(value = ConstsRole.ADMIN)
    public Object updateAppAction(@RequestBody  AppAccountIds appAccountIds  ) {
        App app = appService.load(appAccountIds.getAppKey());
        if(app==null){
            throw new BillException(UnipayError.AppNotExists);
        }
        if (appService.grantPay(appAccountIds)) {
            return new BillApiResponse("应用支付方式授权失败");
        }else{
            throw new BillException(5000, "支付方式授权");
        }

    }

    @ApiOperation(value = "获取有效APP", position = 17, tags = "应用管理")
    @RequestMapping(value = "/getApps", method = {RequestMethod.GET})
    @NeedLogin
    public Object getAppsAction() {
        return new BillApiResponse(appService.getApps(false));
    }

    @ApiOperation(value = "根据APPKEY获取收款账号", position = 19, tags = "应用管理")
    @RequestMapping(value = "/getAppAccountIds", method = {RequestMethod.GET})
    @NeedLogin
    public Object getAppAccountIdsAction(@ApiParam(value = "appkey") @RequestParam String appkey) {
        return appService.getByAppkey(appkey);
    }

    @ApiOperation(value = "获取应用下拉框", position = 18, tags = "应用管理")
    @RequestMapping(value = "/appCombo", method = {RequestMethod.GET})
    @NeedLogin
    public Object appComboAction(@RequestParam(required = false,defaultValue = "true") boolean showDisable){
       return appService.getApps(showDisable).stream().map(es->new BaseComboResponse(es.getAppName(),es.getAppKey())).collect(Collectors.toList());
    }
}
