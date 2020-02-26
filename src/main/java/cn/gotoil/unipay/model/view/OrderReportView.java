package cn.gotoil.unipay.model.view;

import lombok.Getter;
import lombok.Setter;

/**
 * 订单报表模型
 *
 * @author think <syj247@qq.com>、
 * @date 2020-2-25、16:53
 */
@Getter
@Setter
public class OrderReportView  {

    String dateName;
    String appName;
    String payTypeCategoryName;

    /** 总订单数量 **/
    int orderCount;
    /** 总订单金额 **/
    int orderFee ;

    /** 成功订单数量**/
    int paySuccessOrderCount;
    /** 成功订单金额 **/
    int paySuccessOrderFee;

    /** 失败订单数量 **/
    int payFailOrderCount;
    /**  失败订单金额**/
    int payFailOrderFee;
}
