package cn.gotoil.unipay.utils;

import cn.gotoil.unipay.model.enums.EnumPayType;

/**
 * 请求校验验证工具
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-19、16:57
 */
public class UtilValid {

    public static boolean validEnumPayTypeByCode(String payTypeCode) {
        EnumPayType[] types = EnumPayType.values();
        for (EnumPayType t : types) {
            if (t.getCode().equals(payTypeCode)) {
                return true;
            }
        }
        return false;
    }
}
