package com.huaneng.data.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;

public class MeiHuaUtils {

    private static final String[] ZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};

    private static final String[] GUA = {"", "乾", "兑", "离", "震", "巽", "坎", "艮", "坤"};

    public static Map<String, Object> calculate(String dateTime) {

        String[] arr = dateTime.split(" ");

        String[] date = arr[0].split("-");
        String[] time = arr[1].split(":");

        Solar solar = Solar.fromYmdHms(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]),
            Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

        Lunar lunar = solar.getLunar();

        // 年地支序数
        int yearNumber = indexOf(ZHI, lunar.getYearZhi());

        // 农历月份
        int monthNumber = lunar.getMonth();

        // 农历日期
        int dayNumber = lunar.getDay();

        // 时辰地支序数
        int hourNumber = indexOf(ZHI, lunar.getTimeZhi());

        int total = yearNumber + monthNumber + dayNumber + hourNumber;

        // 上卦
        int upper = (yearNumber + monthNumber + dayNumber) % 8;
        if (upper == 0)
            upper = 8;

        // 下卦
        int lower = total % 8;
        if (lower == 0)
            lower = 8;

        // 动爻
        int moving = total % 6;
        if (moving == 0)
            moving = 6;

        Map<String, Object> result = new HashMap<>();

        result.put("lunarDate", lunar.toString());
        result.put("yearZhi", lunar.getYearZhi());
        result.put("hourZhi", lunar.getTimeZhi());

        result.put("yearNumber", yearNumber);
        result.put("monthNumber", monthNumber);
        result.put("dayNumber", dayNumber);
        result.put("hourNumber", hourNumber);

        result.put("upperNumber", upper);
        result.put("lowerNumber", lower);
        result.put("movingYao", moving);

        result.put("upperGua", GUA[upper]);
        result.put("lowerGua", GUA[lower]);

        return result;
    }

    private static int indexOf(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value)) {
                return i + 1;
            }
        }
        throw new IllegalArgumentException("无法识别：" + value);
    }

    public static void main(String[] args) {
        // Map<String, Object> result = MeiHuaUtils.calculate("2026-10-29 21:30:00");
        String date = "2026-10-29";
        LocalDate date1 = DateUtil.stringTolocalDate(date);
        int d = date1.getDayOfWeek().getValue();
        String[] digits = {"零", "一", "二", "三", "四", "五", "六", "日"};
        System.out.println(digits[d]);

        // System.out.println(result);
    }
}