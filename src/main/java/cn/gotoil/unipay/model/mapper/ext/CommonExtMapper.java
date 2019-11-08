package cn.gotoil.unipay.model.mapper.ext;

import cn.gotoil.unipay.utils.SqlUtil.BatchEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonExtMapper {
    int insertBatch(BatchEntity batchEntity);
}
