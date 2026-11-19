package com.huaneng.data.util;

import java.util.*;

import com.nlf.calendar.EightChar;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

public class BaZiUtils {

    private static final Map<String, String> WUXING = new HashMap<>();

    static {
        WUXING.put("甲", "木");
        WUXING.put("乙", "木");
        WUXING.put("丙", "火");
        WUXING.put("丁", "火");
        WUXING.put("戊", "土");
        WUXING.put("己", "土");
        WUXING.put("庚", "金");
        WUXING.put("辛", "金");
        WUXING.put("壬", "水");
        WUXING.put("癸", "水");

        WUXING.put("子", "水");
        WUXING.put("亥", "水");
        WUXING.put("寅", "木");
        WUXING.put("卯", "木");
        WUXING.put("巳", "火");
        WUXING.put("午", "火");
        WUXING.put("申", "金");
        WUXING.put("酉", "金");
        WUXING.put("丑", "土");
        WUXING.put("辰", "土");
        WUXING.put("未", "土");
        WUXING.put("戌", "土");
    }

    public static Map<String, Object> calculate(String dateTime) {

        String[] arr = dateTime.split(" ");

        String[] date = arr[0].split("-");
        String[] time = arr[1].split(":");

        Solar solar = Solar.fromYmdHms(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]),
            Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

        Lunar lunar = solar.getLunar();

        EightChar eightChar = lunar.getEightChar();

        // 四柱
        String year = eightChar.getYear();
        String month = eightChar.getMonth();
        String day = eightChar.getDay();
        String hour = eightChar.getTime();

        List<String> pillars = Arrays.asList(year, month, day, hour);

        Map<String, Integer> wuxingCount = new LinkedHashMap<>();

        wuxingCount.put("木", 0);
        wuxingCount.put("火", 0);
        wuxingCount.put("土", 0);
        wuxingCount.put("金", 0);
        wuxingCount.put("水", 0);

        // 统计八个字对应的五行
        for (String pillar : pillars) {

            for (int i = 0; i < pillar.length(); i++) {

                String character = String.valueOf(pillar.charAt(i));

                String element = WUXING.get(character);

                if (element != null) {
                    wuxingCount.put(element, wuxingCount.get(element) + 1);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();

        result.put("year", year);
        result.put("month", month);
        result.put("day", day);
        result.put("hour", hour);

        result.put("bazi", year + " " + month + " " + day + " " + hour);

        result.put("wuxingCount", wuxingCount);

        return result;
    }

    public static void main(String[] args) {
        Map<String, Object> result = BaZiUtils.calculate("2026-11-05 10:30:00");

        System.out.println(result);
    }
}