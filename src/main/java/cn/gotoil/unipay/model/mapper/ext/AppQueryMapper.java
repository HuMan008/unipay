package cn.gotoil.unipay.model.mapper.ext;


import cn.gotoil.unipay.model.entity.AppChargeAccount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/21.
 */
@Mapper
public interface AppQueryMapper {

    int updateChargeaccountStatusByType(Map map);

    int updateChargeaccountStatusById(Map map);

    List<AppChargeAccount> selectChargeaccountStatusById(Map map);

    List<AppChargeAccount> selectChargeaccountStatusByType(Map map);

    int insert(AppChargeAccount appChargeAccount);

}
