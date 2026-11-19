package com.huaneng.data.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

/**
 * @author mingr
 * @since 2022/12/1 11:23
 */
public class DateUtil {
    /**
     * String 转 LocalDate
     */
    public static LocalDate stringTolocalDate(String date) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, df);
    }

    /**
     * String 转 LocalDateTime
     */
    public static LocalDateTime stringTolocalDateTime(String datetime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(datetime, df);
    }

    /**
     * LocalDate 转 String
     */
    public static String stringTolocalDate(LocalDate date) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return df.format(date);
    }

    /**
     * LocalDateTime 转 String
     */
    public static String localDateTimeToString(LocalDateTime datetime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return df.format(datetime);
    }

    public static Date localdatetimeToDate(LocalDateTime datetime) {
        // Zoneld用于标识在Instant和LocalDateTime之间的转换规则，同时也是时区ID
        // 通过ZoneId的systemDefault获取系统默认时区
        ZoneId zoneId = ZoneId.systemDefault();
        // LocalDateTime只是能显示本地日期和时间，没有时区
        // ZoneIdDateTime要标识一个带时区的日期和时间
        // ZonedDateTime相当于LocalDateTime+ZoneId
        // 通过LocalDateTime的atZone(new ZoneID)把LocalDateTime转换为ZoneDateTime
        ZonedDateTime zonedDateTime = datetime.atZone(zoneId);
        // Instant类用于在应用程序中记录事件的时间戳
        // 也即是把时间转化为一瞬间
        // ZonedDateTime.toInstant是用于把ZonedDateTime转为Instant类型
        Instant instant = zonedDateTime.toInstant();
        // 使用Date.from(new instant)方法,改为Date类型,该日期表示时间轴上与传递的瞬间相同的点。
        Date date = Date.from(instant);
        return date;
    }

    /**
     * String 转 date
     * 
     * @param str
     * @return
     */
    public static Date stringToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 阳历转农历
     *
     * @param solarDate
     *            阳历日期，格式：yyyy-MM-dd
     */
    public static void solarToLunar(String solarDate) {

        String[] dates = solarDate.split("-");

        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);

        // 阳历日期
        Solar solar = new Solar(year, month, day);

        // 转换为农历
        Lunar lunar = solar.getLunar();

        // 农历年份
        int lunarYear = lunar.getYear();

        // 农历月份
        int lunarMonth = lunar.getMonth();

        // 农历日期
        int lunarDay = lunar.getDay();

        System.out.println("农历年份：" + lunarYear);
        System.out.println("农历月份：" + lunarMonth);
        System.out.println("农历日期：" + lunarDay);
        System.out.println("日干支：" + lunar.getDayInGanZhi());

        // 农历中文日期
        System.out.println("农历日期：" + lunar.toString());
    }

    public static JSONObject getLunarDate(String solarDate) {

        String[] arr = solarDate.split("-");
        Solar solar = new Solar(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
        Lunar lunar = solar.getLunar();
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("year", lunar.getYear());
        jsonObject.put("month", lunar.getMonth());
        jsonObject.put("day", lunar.getDay());
        jsonObject.put("ganzhiDay", lunar.getDayInGanZhi());

        jsonObject.put("yearCn", lunar.getYearInGanZhi());
        jsonObject.put("monthCn", lunar.getMonthInChinese());
        jsonObject.put("dayCn", lunar.getDayInChinese());

        return jsonObject;
    }

    public static long getLunarDayOfYear(String solarDate) {

        String[] arr = solarDate.split("-");

        int year = Integer.parseInt(arr[0]);
        int month = Integer.parseInt(arr[1]);
        int day = Integer.parseInt(arr[2]);

        // 阳历日期
        Solar solar = new Solar(year, month, day);

        // 转农历
        Lunar lunar = solar.getLunar();

        // 当前农历年的正月初一
        Solar lunarNewYear = Lunar.fromYmd(lunar.getYear(), 1, 1).getSolar();

        LocalDate current = LocalDate.of(solar.getYear(), solar.getMonth(), solar.getDay());

        LocalDate newYear = LocalDate.of(lunarNewYear.getYear(), lunarNewYear.getMonth(), lunarNewYear.getDay());

        return ChronoUnit.DAYS.between(newYear, current) + 1;
    }

    public static String getWuxing(String key) {
        Map<String, String> wuxing = new HashMap<>();
        wuxing.put("坎", "水");
        wuxing.put("坤", "土");
        wuxing.put("震", "木");
        wuxing.put("巽", "木");
        wuxing.put("中", "中");
        wuxing.put("乾", "金");
        wuxing.put("兑", "金");
        wuxing.put("艮", "土");
        wuxing.put("离", "火");
        return "(" + wuxing.get(key) + ")";
    }

    public static void main(String[] args) {
        // solarToLunar("2026-11-04");
        JSONObject lunarDate = getLunarDate("2026-11-17");
        System.out.println(lunarDate);
    }

    // public static void main(String[] args) {
    // // 1-33 每个数字对应的宫位
    // String[] digits = {"离", "坎", "坤", "震", "巽", "中", "乾", "兑", "艮", "离"};
    // for (int i = 1; i < 34; i++) {
    // int d = i % 9;
    // String gongwei = digits[d];
    // System.out.println("数字 " + i + " : " + gongwei);
    //
    // }
    // }

}
