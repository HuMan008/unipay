package cn.gotoil.unipay.model.mapper;

import cn.gotoil.unipay.model.entity.AppChargeAccount;
import cn.gotoil.unipay.model.entity.AppChargeAccountExample;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AppChargeAccountMapper {
    long countByExample(AppChargeAccountExample example);

    int deleteByExample(AppChargeAccountExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AppChargeAccount record);

    int insertSelective(AppChargeAccount record);

    List<AppChargeAccount> selectByExample(AppChargeAccountExample example);

    AppChargeAccount selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") AppChargeAccount record, @Param("example") AppChargeAccountExample example);

    int updateByExample(@Param("record") AppChargeAccount record, @Param("example") AppChargeAccountExample example);

    int updateByPrimaryKeySelective(AppChargeAccount record);

    int updateByPrimaryKey(AppChargeAccount record);
}