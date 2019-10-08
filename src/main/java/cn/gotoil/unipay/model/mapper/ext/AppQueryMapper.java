package cn.gotoil.unipay.model.mapper.ext;


import cn.gotoil.unipay.model.entity.App;
import cn.gotoil.unipay.model.entity.AppChargeAccount;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/21.
 */
public interface AppQueryMapper {

    List<App> queryList(Map map);

    int queryListCount(Map map);

    int checkName(Map map);

    int updateChargeaccountStatusByType(Map map);

    int updateChargeaccountStatusById(Map map);

    List<AppChargeAccount> selectChargeaccountStatusById(Map map);

    List<AppChargeAccount> selectChargeaccountStatusByType(Map map);

    int insert(AppChargeAccount appChargeAccount);

}
