package cn.gotoil.unipay.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-26、17:13
 */
public class UtilString {
    //取字符串
    public static String getLongString(String str, int maxLen) {
        if (StringUtils.isEmpty(str)) {
            return "";
        } else if (str.length() <= maxLen) {
            return str;
        } else {
            return str.substring(0, maxLen - 1);
        }
    }

}
