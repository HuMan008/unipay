package cn.gotoil.unipay.model.mapper;

import cn.gotoil.unipay.model.entity.ChargeConfig;
import cn.gotoil.unipay.model.entity.ChargeConfigExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChargeConfigMapper {
    long countByExample(ChargeConfigExample example);

    int deleteByExample(ChargeConfigExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ChargeConfig record);

    int insertSelective(ChargeConfig record);

    List<ChargeConfig> selectByExampleWithBLOBs(ChargeConfigExample example);

    List<ChargeConfig> selectByExample(ChargeConfigExample example);

    ChargeConfig selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ChargeConfig record, @Param("example") ChargeConfigExample example);

    int updateByExampleWithBLOBs(@Param("record") ChargeConfig record, @Param("example") ChargeConfigExample example);

    int updateByExample(@Param("record") ChargeConfig record, @Param("example") ChargeConfigExample example);

    int updateByPrimaryKeySelective(ChargeConfig record);

    int updateByPrimaryKeyWithBLOBs(ChargeConfig record);

    int updateByPrimaryKey(ChargeConfig record);
}