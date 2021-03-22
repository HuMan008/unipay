package cn.gotoil.unipay.utils;

import cn.gotoil.bill.tools.date.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Administrator on 2018/2/22.
 */
public class DateUtil extends DateUtils {
    static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private static ThreadLocal<SimpleDateFormat> threadLocalRFC3339Formatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(RFC3339);
        }
    };

    public static final String NYR = "yyyy-MM-dd";
    public static final String NYRSFM = "yyyy-MM-dd HH:mm:ss";
    public static final String NYRSFM1 = "yyyyMMddHHmmss";
    public static final String RFC3339 = "YYYY-MM-DD'T'HH:mm:ssXXX";


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
        SimpleDateFormat simpleDateFormat = fetchSimpleDateFormatter(NYR);
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
        SimpleDateFormat simpleDateFormat = fetchSimpleDateFormatter(NYR);
        return simpleDateFormat.format(endTime);
    }

    public static Long getTomorrowMin() {
        try {
            SimpleDateFormat nyr = DateUtils.simpleDateFormatter();
            SimpleDateFormat nyrsfm = DateUtils.simpleDatetimeFormatter();
            Date d1 = new Date();
            String end = nyr.format(d1) + " 23:59:59";
            Date d2 = nyrsfm.parse(end);
            Long number = (d2.getTime() - d1.getTime()) / (1000 * 60);
            number = number + 2879;
            return number;
        } catch (Exception e) {
            logger.error("提油码计算时间出错:", e);
            return null;
        }
    }

    /**
     * 根据数字计算日期
     *
     * @param day
     * @return
     */
    public static String getByDay(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, day);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(NYR);
        Date endTime = cal.getTime();
        return simpleDateFormat.format(endTime);
    }


   /* public static void main(String[] as){
        System.out.println( DateUtil.getByDay(1));
    }*/
}
