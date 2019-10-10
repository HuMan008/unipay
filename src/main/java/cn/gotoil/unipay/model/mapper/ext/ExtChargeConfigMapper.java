package cn.gotoil.unipay.model.mapper.ext;

import cn.gotoil.unipay.model.entity.ChargeConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExtChargeConfigMapper {
    int insertChargeConfig(ChargeConfig chargeConfig);
}
