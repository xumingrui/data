package com.huaneng.data.util;

import java.util.HashMap;
import java.util.Map;

import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

public class LuoShuUtils {

    private static final String[] ZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    private static final Map<Integer, String> WUXING = new HashMap<>();

    static {
        WUXING.put(1, "水");
        WUXING.put(2, "土");
        WUXING.put(3, "木");
        WUXING.put(4, "木");
        WUXING.put(5, "土");
        WUXING.put(6, "金");
        WUXING.put(7, "金");
        WUXING.put(8, "土");
        WUXING.put(9, "火");
    }

    public static Map<String, Object> calculate(String dateTime) {

        String[] arr = dateTime.split(" ");

        String[] date = arr[0].split("-");
        String[] time = arr[1].split(":");

        Solar solar = Solar.fromYmdHms(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]),
            Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

        Lunar lunar = solar.getLunar();

        int year = indexOf(lunar.getYearZhi());
        int month = lunar.getMonth();
        int day = lunar.getDay();
        int hour = indexOf(lunar.getTimeZhi());

        int total = year + month + day + hour;

        int number = total % 9;

        if (number == 0) {
            number = 9;
        }

        Map<String, Object> result = new HashMap<>();

        result.put("yearNumber", year);
        result.put("monthNumber", month);
        result.put("dayNumber", day);
        result.put("hourNumber", hour);

        result.put("totalNumber", total);

        result.put("luoShuNumber", number);

        result.put("wuxing", WUXING.get(number));

        return result;
    }

    private static int indexOf(String value) {

        for (int i = 0; i < ZHI.length; i++) {

            if (ZHI[i].equals(value)) {
                return i + 1;
            }
        }

        throw new IllegalArgumentException("无法识别地支：" + value);
    }

    public static void main(String[] args) {
        Map<String, Object> result = LuoShuUtils.calculate("2026-11-03 10:30:00");

        System.out.println(result);
    }
}