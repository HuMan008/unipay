package cn.gotoil.unipay.model.mapper;


import cn.gotoil.unipay.model.entity.NotifyAccept;
import cn.gotoil.unipay.model.entity.NotifyAcceptExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotifyAcceptMapper {
    long countByExample(NotifyAcceptExample example);

    int deleteByExample(NotifyAcceptExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(NotifyAccept record);

    int insertSelective(NotifyAccept record);

    List<NotifyAccept> selectByExampleWithBLOBs(NotifyAcceptExample example);

    List<NotifyAccept> selectByExample(NotifyAcceptExample example);

    NotifyAccept selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") NotifyAccept record, @Param("example") NotifyAcceptExample example);

    int updateByExampleWithBLOBs(@Param("record") NotifyAccept record, @Param("example") NotifyAcceptExample example);

    int updateByExample(@Param("record") NotifyAccept record, @Param("example") NotifyAcceptExample example);

    int updateByPrimaryKeySelective(NotifyAccept record);

    int updateByPrimaryKeyWithBLOBs(NotifyAccept record);

    int updateByPrimaryKey(NotifyAccept record);
}