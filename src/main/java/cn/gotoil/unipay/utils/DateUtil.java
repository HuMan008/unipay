package cn.gotoil.unipay.utils;

import cn.gotoil.bill.tools.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2018/2/22.
 */
public class DateUtil {
    static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    public static final String nyr = "yyyy-MM-dd";
    public static final String nyrsfm = "yyyy-MM-dd HH:mm:ss";
    public static final String nyrsfm1 = "yyyyMMddHHmmss";

    public static String formatByNyr(Date date) {
        return formatByCustom(date, nyr);
    }

    public static String formatByNyrsfm(Date date) {
        return formatByCustom(date, nyrsfm);
    }

    public static String formatByNyrsfm1(Date date) {
        return formatByCustom(date, nyrsfm1);
    }

    public static String formatByCustom(Date date, String format) {
        if (date == null || StringUtils.isEmpty(format)) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static Date stringtoDateByNyr(String time) {
        return stringtoDate(time, nyr);
    }

    public static Date stringtoDateByNyrsfm(String time) {
        return stringtoDate(time, nyrsfm);
    }

    public static Date stringtoDateByNyrsfm1(String time) {
        return stringtoDate(time, nyrsfm1);
    }

    public static Date stringtoDate(String time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(time);
        } catch (Exception e) {
            logger.error("日期格式转换错误", e);
            return null;
        }
    }
    /**
     * 获取当月的最后1天
     *
     * @return
     */
    public static String getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.roll(Calendar.DATE, -1);
        Date endTime = cal.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(nyr);
        return simpleDateFormat.format(endTime);
    }

    /**
     * 获取当月第1天
     *
     * @return
     */
    public static String getMonthBegin() {
        Calendar cal = Calendar.getInstance();
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        Date endTime = cal.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(nyr);
        return simpleDateFormat.format(endTime);
    }

    public static Long getTomorrowMin(){
        try{
            SimpleDateFormat nyr = DateUtils.simpleDateFormatter();
            SimpleDateFormat nyrsfm = DateUtils.simpleDatetimeFormatter();
            Date d1 = new Date();
            String end = nyr.format(d1)+" 23:59:59";
            Date d2 = nyrsfm.parse(end);
            Long number = (d2.getTime() -d1.getTime()) / (1000 * 60);
            number = number +2879;
            return number;
        }catch (Exception e){
            logger.error("提油码计算时间出错:", e);
            return null;
        }
    }

    /**
     * 根据数字计算日期
     * @param day
     * @return
     */
    public static String getByDay(int day){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, day);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(nyr);
        Date endTime = cal.getTime();
        return simpleDateFormat.format(endTime);
    }


   /* public static void main(String[] as){
        System.out.println( DateUtil.getByDay(1));
    }*/
}
