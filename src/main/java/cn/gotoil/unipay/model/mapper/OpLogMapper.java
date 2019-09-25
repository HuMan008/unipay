package cn.gotoil.unipay.model.mapper;


import cn.gotoil.unipay.model.entity.OpLog;
import cn.gotoil.unipay.model.entity.OpLogExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OpLogMapper {
    long countByExample(OpLogExample example);

    int deleteByExample(OpLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OpLog record);

    int insertSelective(OpLog record);

    List<OpLog> selectByExample(OpLogExample example);

    OpLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OpLog record, @Param("example") OpLogExample example);

    int updateByExample(@Param("record") OpLog record, @Param("example") OpLogExample example);

    int updateByPrimaryKeySelective(OpLog record);

    int updateByPrimaryKey(OpLog record);
}