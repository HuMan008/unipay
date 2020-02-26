package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.bill.web.annotation.HasRole;
import cn.gotoil.bill.web.annotation.NeedLogin;
import cn.gotoil.bill.web.message.BillApiResponse;
import cn.gotoil.unipay.config.consts.ConstsRole;
import cn.gotoil.unipay.model.OrderRefundNotifyBean;
import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.enums.EnumPayCategory;
import cn.gotoil.unipay.model.view.OrderReportView;
import cn.gotoil.unipay.web.message.request.admin.OrderReportRequest;
import cn.gotoil.unipay.web.message.response.admin.BaseComboResponse;
import cn.gotoil.unipay.web.services.AppService;
import cn.gotoil.unipay.web.services.ReportService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 报表
 *
 * @author think <syj247@qq.com>、
 * @date 2020-2-25、16:28
 */
@RequestMapping("web/report")
@RestController
public class ReportController {
    @Autowired
    ReportService reportService;
    @Autowired
    AppService appService;

    @RequestMapping(value = "order", method = RequestMethod.POST)
    @NeedLogin
    @HasRole(values = {ConstsRole.ADMIN,ConstsRole.ORDER,ConstsRole.FINANCE})
    public Object orderReportAction(@RequestBody OrderReportRequest orderReportRequest) {
        List<App> appList = appService.getApps(true);
        Map<String,String> appMap = appList.stream().collect(Collectors.toMap(App::getAppKey,App::getAppName));

        Map<String,String> payTypeCategoryMap =
                BaseComboResponse.getPayTypeCategoryCodeCombo().stream().collect(Collectors.toMap(BaseComboResponse::getValue,BaseComboResponse::getLabel));

        List<OrderReportView> list =   reportService.orderReport(orderReportRequest.getParams());
        List<String> columns =new ArrayList<>();
        List<Map<String,String>> rows  = new ArrayList<>();
        for( int i = 0 ;i<list.size();i++){
            Map<String,String> map = new HashMap<>();
            if(StringUtils.isNotEmpty(list.get(i).getAppName())){
                map.put("应用名称",appMap.get(list.get(i).getAppName()));
                if(i==0){
                    columns.add("应用名称");
                }
            }
            if(StringUtils.isNotEmpty(list.get(i).getDateName())){
                map.put("日期",list.get(i).getDateName());
                if(i==0){
                    columns.add("日期");
                }
            }
            if(StringUtils.isNotEmpty(list.get(i).getPayTypeCategoryName())){
                map.put("支付方式",payTypeCategoryMap.get(list.get(i).getPayTypeCategoryName()));
                if(i==0){
                    columns.add("支付方式");
                }
            }
            map.put("下单总数",String.valueOf(list.get(i).getOrderCount()));
            map.put("下单总金额",new BigDecimal(list.get(i).getOrderFee()).divide(new BigDecimal(100),2,
                    RoundingMode.HALF_UP).toString());
            map.put("成功订单数量",String.valueOf(list.get(i).getPaySuccessOrderCount()));
            map.put("成功订单金额",new BigDecimal(list.get(i).getPaySuccessOrderFee()).divide(new BigDecimal(100),2,
                    RoundingMode.HALF_UP).toString());
            map.put("失败订单数量",String.valueOf(list.get(i).getPayFailOrderCount()));
            map.put("失败订单金额",new BigDecimal(list.get(i).getPayFailOrderFee()).divide(new BigDecimal(100),2,
                    RoundingMode.HALF_UP).toString());
            map.put("订单成功率",
                    new BigDecimal(list.get(i).getPaySuccessOrderCount()).divide(new BigDecimal(list.get(i).getOrderCount()),2,RoundingMode.HALF_UP).toString());
            rows.add(map);
        }
        columns.add("下单总数");
        columns.add("下单总金额");
        columns.add("成功订单数量");
        columns.add("成功订单金额");
        columns.add("失败订单数量");
        columns.add("失败订单金额");
        columns.add("订单成功率");

        Map<String,Object> map = new HashMap<>();
        map.put("rows",rows);
        map.put("columns",columns);
        return map;

    }
}
