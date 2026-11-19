package com.huaneng.data.util;

import java.util.HashMap;
import java.util.Map;

public class YiJingNumberUtils {

    private static final String[] GUA = {"", "乾", "兑", "离", "震", "巽", "坎", "艮", "坤"};

    public static Map<String, Object> calculate(String dateTime) {

        String[] arr = dateTime.split(" ");

        String[] date = arr[0].split("-");
        String[] time = arr[1].split(":");

        int year = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);

        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        int second = Integer.parseInt(time[2]);

        // 上卦
        int upper = (year + month + day) % 8;

        if (upper == 0)
            upper = 8;

        // 下卦
        int lower = (year + month + day + hour + minute) % 8;

        if (lower == 0)
            lower = 8;

        // 动爻
        int moving = (year + month + day + hour + minute + second) % 6;

        if (moving == 0)
            moving = 6;

        Map<String, Object> result = new HashMap<>();

        result.put("upperNumber", upper);
        result.put("lowerNumber", lower);
        result.put("movingYao", moving);

        result.put("upperGua", GUA[upper]);
        result.put("lowerGua", GUA[lower]);

        return result;
    }
}