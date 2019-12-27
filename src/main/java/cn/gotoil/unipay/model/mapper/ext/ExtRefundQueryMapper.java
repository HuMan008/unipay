package cn.gotoil.unipay.model.mapper.ext;

import cn.gotoil.unipay.model.entity.Refund;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExtRefundQueryMapper {
    List<Refund> refundList(Map<String, Object> params);
    int  queryRefundCounts(Map<String,Object> params);
}
