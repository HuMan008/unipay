package cn.gotoil.unipay.web.services;

import cn.gotoil.unipay.model.view.OrderReportView;

import java.util.List;
import java.util.Map;

/**
 * @author think <syj247@qq.com>、
 * @date 2020-2-25、19:09
 */
public interface ReportService {
    List<OrderReportView> orderReport(Map<String, String> param);
}
