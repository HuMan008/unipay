package cn.gotoil.unipay.utils;

import cn.gotoil.bill.exception.BillError;
import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;

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

    public static String toUtf8(String msg){
        if(StringUtils.isEmpty(msg)){
            return "";
        }
        String xx = "";
        try {
            xx =URLEncoder.encode(msg,
                    Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {

        }
        return xx;
    }



    public synchronized static boolean checkObjFieldIsNull(Object obj) {
        try {
            boolean flag = false;
            for (Field f : obj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(obj) == null || "".equals(f.get(obj))) {
                    flag = true;
                    return flag;
                }
            }
            return flag;
        } catch (Exception e) {
            return true;
        }
    }
}
