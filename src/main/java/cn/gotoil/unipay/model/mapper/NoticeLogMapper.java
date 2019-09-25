package cn.gotoil.unipay.model.mapper;

import cn.gotoil.unipay.model.entity.NoticeLog;
import cn.gotoil.unipay.model.entity.NoticeLogExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeLogMapper {
    long countByExample(NoticeLogExample example);

    int deleteByExample(NoticeLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(NoticeLog record);

    int insertSelective(NoticeLog record);

    List<NoticeLog> selectByExample(NoticeLogExample example);

    NoticeLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") NoticeLog record, @Param("example") NoticeLogExample example);

    int updateByExample(@Param("record") NoticeLog record, @Param("example") NoticeLogExample example);

    int updateByPrimaryKeySelective(NoticeLog record);

    int updateByPrimaryKey(NoticeLog record);
}