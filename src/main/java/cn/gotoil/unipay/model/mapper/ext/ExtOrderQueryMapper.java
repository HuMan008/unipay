package cn.gotoil.unipay.model.mapper.ext;

import cn.gotoil.unipay.model.entity.Order;
import cn.gotoil.unipay.model.view.OrderView;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface ExtOrderQueryMapper {
    List<OrderView> queryOrder(Map<String,Object> params);

    int queryOrderCounts(Map<String,Object> params);

    List<Order> queryOrderByIn10();

    List<Order> queryOrderByOut10();
}
