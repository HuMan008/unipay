package cn.gotoil.unipay.web.message.request.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单报表请求
 *
 * @author think <syj247@qq.com>、
 * @date 2020-2-25、16:32
 */

@Data
@EqualsAndHashCode
public class OrderReportRequest  {
    private Map params = new HashMap();
}
