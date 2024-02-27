package com.mindskip.xzs.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;


/**
 * @version 3.5.0
 * @description:  The type Date time util.
 * Copyright (C), 2020-2021, 武汉思维跳跃科技有限公司
 * @date 2021/12/25 9:45
 */
public class DateTimeUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);
    /**
     * The constant STANDER_FORMAT.
     */
    public static final String STANDER_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * The constant STANDER_FORMAT.
     */
    public static final String STANDER_FORMATFull = "yyyyMMddHHmmss";
    /**
     * The constant STANDER_SHORT_FORMAT.
     */
    public static final String STANDER_SHORT_FORMAT = "yyyy-MM-dd";
    /**
     * The constant STANDER_SHORT_FORMAT.
     */
    public static final String STANDER_SHORT_MD = "MM月dd日";

    /**
     * Add duration date.
     *
     * @param date     the date
     * @param duration the duration
     * @return the date
     */
    public static Date addDuration(Date date, Duration duration) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.SECOND, (int) duration.getSeconds());
        return ca.getTime();
    }

    /**
     * Date format string.
     *
     * @param date the date
     * @return the string
     */
    public static String dateFormat(Date date) {
        if (null == date) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat(STANDER_FORMAT);
        return dateFormat.format(date);
    }
    /**
     * Date format string.
     *
     * @param date the date
     * @return the string
     */
    public static String dateFormatFull(Date date) {
    	if (null == date) {
    		return "";
    	}
    	DateFormat dateFormat = new SimpleDateFormat(STANDER_FORMATFull);
    	return dateFormat.format(date);
    }

    /**
     * Date short format string.
     *
     * @param date the date
     * @return the string
     */
    public static String dateShortFormat(Date date) {
        if (null == date) {
            return "";
        }
        DateFormat dateFormat = new SimpleDateFormat(STANDER_SHORT_FORMAT);
        return dateFormat.format(date);
    }

    /**
     * Parse date.
     *
     * @param dateStr the date str
     * @param format  the format
     * @return the date
     */
    public static Date parse(String dateStr, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Gets month start day.
     *
     * @return the month start day
     */
    public static Date getMonthStartDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        String dateStr = formatter.format(cale.getTime());
        return parse(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * Gets month end day.
     *
     * @return the month end day
     */
    public static Date getMonthEndDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 1);
        cale.set(Calendar.DAY_OF_MONTH, 0);
        String dateStr = formatter.format(cale.getTime());
        return parse(dateStr, STANDER_FORMAT);
    }


    /**
     * Moth start to now format list.
     *
     * @return the list
     */
    public static List<String> MothStartToNowFormat() {
        Date startTime = getMonthStartDay();
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(new Date());
        int mothDayCount = nowCalendar.get(Calendar.DAY_OF_MONTH);
        List<String> mothDays = new ArrayList<>(mothDayCount);
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        mothDays.add(formatter.format(startTime));
        for (int i = 0; i < mothDayCount - 1; i++) {
            startCalendar.add(Calendar.DATE, 1);
            Date end_date = startCalendar.getTime();
            mothDays.add(formatter.format(end_date));
        }
        return mothDays;
    }


    /**
     * Moth day list.
     *
     * @return the list
     */
    public static List<String> MothDay() {
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(getMonthEndDay());
        int endMothDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        List<String> list = new ArrayList<>(endMothDay);
        for (int i = 1; i <= endMothDay; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }
    
    /**
     * 计算这个时间的间隔天数
     * @param startDate
     * @param endDate
     * @return
     */
    public static int DayBettew(String startDateStr,String endDateStr) {
    	Date startDate=parse(startDateStr, STANDER_SHORT_FORMAT);
    	Date endDate=parse(endDateStr, STANDER_SHORT_FORMAT);
    	long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        int days = (int) ((endTime - startTime) / (1000 * 60 * 60 * 24));
        return days;
	}
    /**
     * Date format string.
     *
     * @param date the date
     * @return the string
     */
    public static String dateFormatMD(Date date) {
    	if (null == date) {
    		return "";
    	}
    	DateFormat dateFormat = new SimpleDateFormat(STANDER_SHORT_MD);
    	return dateFormat.format(date);
    }
    /**
     * 返回周
     * 
     * @param d
     * @return
     *
     * @变更记录 2024年2月27日 下午4:18:51 武林林 创建
     *
     */
    public static String getWeek(Date date) {
    	String newStr=dateShortFormat(date);
    	
		HashMap<String, String > ret  = new HashMap<String, String>(){{  
			put("2024-02-19","1");
			put("2024-02-26","2");
			put("2024-03-04","3");
			put("2024-03-11","4");
			put("2024-03-18","5");
			put("2024-03-25","6");
			put("2024-04-01","7");
			put("2024-04-08","8");
			put("2024-04-15","9");
			put("2024-04-22","10");
			put("2024-04-29","11");
			put("2024-05-06","12");
			put("2024-05-13","13");
			put("2024-05-20","14");
			put("2024-05-27","15");
			put("2024-06-03","16");
			put("2024-06-10","17");
			put("2024-06-17","18");
			put("2024-06-24","19");
		}}; 

    	return ret.get(newStr);
    }

}
