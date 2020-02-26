package cn.gotoil.unipay.model.mapper.ext;

import cn.gotoil.bill.exception.BillException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by think on 2017/6/16.
 */
public class UnipaySqlProvider {


    public static final char UNDERLINE = '_';


    /*public static void main(String[] args) {
        Map<String,Object> map =new HashMap<>();
        map.put("appOrderNo","abc");
        map.put("appOrderNo1","abcd");
        map.put("appOrderNo1","abcd");
        map.put("state", EnumOrderStatus.PayedSuccess.getCode());
        UnionpaySqlProvider bb = new UnionpaySqlProvider();
        String x =bb.buildUnionOrderQuerySql(map,true);
        System.out.println(x);

    }*/

    public static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public String buildOrderReportSql(Map<String, Object> paramMap) {
        //
        //        appId: this.searchForm.appId,
        //                beginTime: this.searchForm.beginTime,
        //                endTime: this.searchForm.endTime,
        //                payTypeCategory: this.searchForm.payTypeCategory,
        //                payTypeCategoryGroupBy: this.searchForm.payTypeCategoryGroupBy,
        //                dateGroupBy: this.searchForm.dateGroupBy,
        //                appGroupBy: this.searchForm.appGroupBy
        StringBuilder groupBy = new StringBuilder(" group by ");
        List<String> groupByList = new ArrayList();
        StringBuilder wheresql = new StringBuilder(" where 1=1 ");
        StringBuilder selectFields = new StringBuilder("SELECT " + "COUNT(1) AS orderCount,SUM(fee) AS orderFee, " +
                "COUNT( CASE WHEN o.status= 0 THEN 1 ELSE NULL END) AS paySuccessOrderCount," + "SUM( CASE WHEN o" +
                ".status= 0 THEN o.fee ELSE 0 END) AS paySuccessOrderFee," + "COUNT( CASE WHEN o.status =  2 THEN 1 " +
                "ELSE NULL END) AS payFailOrderCount," + "SUM( CASE WHEN o.status =  2 THEN o.fee ELSE 0 END) AS " +
                "payFailOrderFee ");


        int limit = 0;
        if ("Y".equalsIgnoreCase(paramMap.get("dateGroupBy").toString())) {
            groupByList.add(" year(o.created_at) ");
            selectFields.append(" ,year(o.created_at) as dateName ");
        } else if ("M".equalsIgnoreCase(paramMap.get("dateGroupBy").toString())) {
            groupByList.add(" DATE_FORMAT(o.created_at ,'%Y-%m') ");
            selectFields.append(" ,DATE_FORMAT(o.created_at,'%Y-%m') as dateName ");
            limit = 12;
        } else if ("D".equalsIgnoreCase(paramMap.get("dateGroupBy").toString())) {
            groupByList.add(" date(o.created_at) ");
            selectFields.append(" ,date(o.created_at) as dateName ");
        }
        if ("0".equalsIgnoreCase(paramMap.get("appGroupBy").toString())) {
            groupByList.add("o.app_id ");
            selectFields.append(" , o.app_id  as appName ");
        }
        if ("0".equalsIgnoreCase(paramMap.get("payTypeCategoryGroupBy").toString())) {
            groupByList.add(" o.pay_type_category ");
            selectFields.append(" ,o.pay_type_category as payTypeCategoryName");
        }
        List<String> appIds = (List<String>) paramMap.get("appId");
        if (! appIds.isEmpty()) {
            wheresql.append("and o.app_id in ('").append(String.join("','",appIds)).append("')");
        }

        List<String> payTypeCategarys = (List<String>) paramMap.get("payTypeCategory");
        if (! payTypeCategarys.isEmpty()) {
            wheresql.append("and o.pay_type_category in ('").append(String.join("','",payTypeCategarys)).append("')");
        }

        if (StringUtils.isNotEmpty(paramMap.get("beginTime").toString())) {
            wheresql.append(" and o.created_at >='").append(paramMap.get("beginTime")).append("'");
        }

        if (StringUtils.isNotEmpty(paramMap.get("endTime").toString())) {
            wheresql.append(" and o.created_at <='").append(paramMap.get("endTime")).append("'");
        }


        if(groupByList.isEmpty()){
            throw new BillException(4999,"必须选择一个分组");
        }
       String  sql  = selectFields.toString()+ " from dp_order o " + wheresql + " "+ groupBy.append(String.join(",",
               groupByList)) ;

        return sql;
    }



}




