package cn.gotoil.unipay.model.mapper.ext;

import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.view.OrderReportView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * 报表自定义SQL
 *
 * @author think <syj247@qq.com>、
 * @date 2020-2-25、16:43
 */
@Mapper
public interface ReportExtMapper {

    @SelectProvider(type = UnipaySqlProvider.class, method = "buildOrderReportSql")
    List<OrderReportView> orderReport(Map<String, String> map);
}
