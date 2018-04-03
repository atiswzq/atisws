package com.atis.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by xshd000 on 2017/2/20.
 */
public class DateUtil {
    /** 年月日时分秒(无下划线) yyyyMMddHHmmss */
    public static final String dtLong                  = "yyyyMMddHHmmss";

    /** 完整时间 yyyy-MM-dd HH:mm:ss */
    public static final String simple                  = "yyyy-MM-dd HH:mm:ss";

    /** 年月日(无下划线) yyyyMMdd */
    public static final String dtShort                 = "yyyyMMdd";

    public static final String dtStMonth                 = "yyyy-MM";

    /** 年月日(无下划线) yyyyMMdd */
    public static final String dtStShort                 = "yyyy-MM-dd";

    /** 年月日(无下划线) yyyyMMdd */
    public static final String dtDate                 = "yyyy-MM-dd";
    /**HH:mm  yyyy/MM/dd*/
    public static final String orderTime                  ="HH:mm  yyyy/MM/dd";

    public static final String dtYear                  ="yyyy";

    public static final String dtMonth                  ="yyyy-MM";

    public static String getDtYear(String date) {
        try {
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df = new SimpleDateFormat(dtYear);
            return df.format(df1.parse(date));
        }catch (Exception e){
            return null;
        }
    }

    public static String getDtMonth(String date){
        try {
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df = new SimpleDateFormat(dtMonth);
            return df.format(df1.parse(date));
        }catch (Exception e){
            return null;
        }
    }

    public static String getDtDate(String date){
        try {
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df = new SimpleDateFormat(dtDate);
            return df.format(df1.parse(date));
        }catch (Exception e){
            return null;
        }
    }



    /**
     * 返回系统当前时间(精确到毫秒),作为一个唯一的订单编号
     * @return
     *      以yyyyMMddHHmmss为格式的当前系统时间
     */
    public  static String getOrderNum(){
        Date date=new Date();
        DateFormat df=new SimpleDateFormat(dtLong);
        return df.format(date);
    }

    /**
     * 获取系统当前日期(精确到毫秒)，格式：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public  static String getDateFormatter(){
        Date date=new Date();
        DateFormat df=new SimpleDateFormat(simple);
        return df.format(date);
    }

    /**
     * 获取系统当期年月日(精确到天)，格式：yyyyMMdd
     * @return
     */
    public static String getDate(){
        Date date=new Date();
        DateFormat df=new SimpleDateFormat(dtShort);
        return df.format(date);
    }

    public static String getDate(Date date){
        DateFormat df=new SimpleDateFormat(dtStShort);
        return df.format(date);
    }

    public static String getMonth(Date date){
        DateFormat df=new SimpleDateFormat(dtStMonth);
        return df.format(date);
    }
    /**
     * 产生随机的三位数
     * @return
     */
    public static String getThree(){
        Random rad=new Random();
        return rad.nextInt(1000)+"";
    }

    /**
     * 获取系统当期年月日(精确到天)，格式：yyyy-MM-dd
     * @return
     */
    public static String getStandDate(){
        Date date=new Date();
        DateFormat df=new SimpleDateFormat(dtDate);
        return df.format(date);
    }


    public static String getStandMonth(){
        Date date=new Date();
        DateFormat df=new SimpleDateFormat(dtMonth);
        return df.format(date);
    }

    public static String getStandYear(){
        Date date=new Date();
        DateFormat df=new SimpleDateFormat(dtYear);
        return df.format(date);
    }


    public static String getDateAfterTen(){
        SimpleDateFormat format = new   SimpleDateFormat(simple);

        Date dd = new Date();

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(dd);

        calendar.add(Calendar.DATE,10);

        String T1 = format.format(calendar.getTime() ) ;
        return T1;
    }

    public static String getMinuteAfterTen(String date) throws ParseException {
        SimpleDateFormat format = new   SimpleDateFormat(simple);

        Date dataFormat = format.parse(date);

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(dataFormat);

        calendar.add(Calendar.MINUTE,10);

        String T1 = format.format(calendar.getTime() ) ;
        return T1;
    }
    public static String getOrderTime(String date){
        SimpleDateFormat format = new SimpleDateFormat(orderTime);
        return format.format(date);
    }
    public static Long getExpiredTime(String date) throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(new SimpleDateFormat(simple).parse(date));
        return c.getTimeInMillis()/1000;
    }
}
