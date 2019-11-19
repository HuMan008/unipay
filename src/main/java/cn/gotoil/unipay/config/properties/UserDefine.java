package cn.gotoil.unipay.config.properties;

import cn.gotoil.bill.model.BaseAdminUser;
import com.google.common.base.Splitter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 用户定义
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-26、10:23
 */
@Setter
@Getter
public class UserDefine extends BaseAdminUser {
    String code;
    String pwd;
    String token;


    public static void fill(UserDefine userDefine) {
        userDefine.setUpwd(userDefine.getPwd());
        if(StringUtils.isNotEmpty(userDefine.getRoleStr())){
            userDefine.setRoles(new HashSet<>(Splitter.on(",").omitEmptyStrings().splitToList(userDefine.getRoleStr())));
        }else{
            userDefine.setRoles(new HashSet<>());
        }
        if(StringUtils.isNotEmpty(userDefine.getPermissionStr())){
            userDefine.setPermissions(new HashSet<>(Splitter.on(",").omitEmptyStrings().splitToList(userDefine.getPermissionStr())));
        }else{
            userDefine.setPermissions(new HashSet<>());
        }
    }
}
