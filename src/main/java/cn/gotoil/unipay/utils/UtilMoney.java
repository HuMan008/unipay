package cn.gotoil.unipay.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * 金额工具
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-23、14:10
 */
public class UtilMoney {

    public static Pattern YuanMoneyPattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
    //    public static Pattern YuanMoneyPattern =Pattern.compile("^(\\d*)(.?)(\\d{2})$/");
    //    public static Pattern YuanMoneyPattern =Pattern.compile("^-?([0-9]+|[0-9]{1,3}(,[0-9]{3})*)(.[0-9]{1,2})?$ ");

    /**
     * 元-->分
     *
     * @param y
     * @return
     */
    public static int YaunToFen(int y) {
        return y * 100;
    }

    /**
     * 元-->分
     * 元最多2位小数
     *
     * @param yuanStr 字符中
     * @return
     */
    public static int yuanToFen(String yuanStr) {
        yuanStr = StringUtils.trim(yuanStr.replaceAll("元", ""));

        if (!YuanMoneyPattern.matcher(yuanStr).matches()) {
            return 0;
        }
        BigDecimal bigDecimal = new BigDecimal(yuanStr).multiply(new BigDecimal(100));
        bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.intValue();
    }


    /**
     * 分转元
     *
     * @param fenValue
     * @param disUnit
     * @return
     */
    public static String fenToYuan(int fenValue, boolean disUnit) {
        String vv = BigDecimal.valueOf(Long.valueOf(fenValue)).divide(new BigDecimal(100)).toString();
        return disUnit ? vv + "元" : vv;
    }

    public static void main(String[] args) {
        //        System.out.println(yuanToFen("0.02元"));
        //        System.out.println(yuanToFen("0.02"));
        //        System.out.println(yuanToFen("0.2"));
        //        System.out.println(yuanToFen("0.002"));
        //        System.out.println(yuanToFen("0.009"));
        //        System.out.println(fenToYuan(1003,true));

    }
}
