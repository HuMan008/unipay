package cn.gotoil.unipay.web.services.impl;

import cn.gotoil.unipay.model.mapper.ext.ReportExtMapper;
import cn.gotoil.unipay.model.view.OrderReportView;
import cn.gotoil.unipay.web.services.ReportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 报表服务实现
 *
 * @author think <syj247@qq.com>、
 * @date 2020-2-25、16:35
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Resource
    ReportExtMapper reportExtMapper;

    @Override
    public List<OrderReportView> orderReport(Map<String, String> param){
        return reportExtMapper.orderReport(param);
    }
}
