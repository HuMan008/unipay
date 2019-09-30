package cn.gotoil.unipay.utils;

import cn.gotoil.bill.exception.BillError;
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


    public static String makeErrorPage(BillError billError) {
        return "redirect:/web/error?errorCode=" + billError.getCode() + "&errorMsg=" + billError.getMessage();
    }

    public static String makeErrorPage(String errorCode, String errorMsg) {
        return "redirect:/web/error?errorCode=" + errorCode + "&errorMsg=" + errorMsg;
    }

    public static String makeErrorPage(int errorCode, String errorMsg) {
        return "redirect:/web/error?errorCode=" + errorCode + "&errorMsg=" + errorMsg;
    }
}
