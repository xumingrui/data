package com.huaneng.data.util;

import java.util.*;

public class LuckyUtils {

    private static final Map<String, String> COLORS = new HashMap<>();

    private static final Map<String, String> DIRECTIONS = new HashMap<>();

    static {

        COLORS.put("木", "绿色、青色");
        COLORS.put("火", "红色、紫色");
        COLORS.put("土", "黄色、棕色");
        COLORS.put("金", "白色、金色");
        COLORS.put("水", "黑色、蓝色");

        DIRECTIONS.put("1", "北方");
        DIRECTIONS.put("2", "西南方");
        DIRECTIONS.put("3", "东方");
        DIRECTIONS.put("4", "东南方");
        DIRECTIONS.put("5", "中央");
        DIRECTIONS.put("6", "西北方");
        DIRECTIONS.put("7", "西方");
        DIRECTIONS.put("8", "东北方");
        DIRECTIONS.put("9", "南方");
    }

    /**
     * 每日幸运数据
     */
    public static Map<String, Object> calculate(int upper, int lower, int moving, int luoShuNumber, String wuxing) {

        // 幸运数字去重
        Set<Integer> numbers = new LinkedHashSet<>();

        numbers.add(upper);
        numbers.add(lower);
        numbers.add(moving);
        numbers.add(luoShuNumber);

        // 衍生数字
        numbers.add((upper + lower) % 9 == 0 ? 9 : (upper + lower) % 9);

        numbers.add((upper * lower) % 9 == 0 ? 9 : (upper * lower) % 9);

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("numbers", new ArrayList<>(numbers));

        result.put("color", COLORS.get(wuxing));

        result.put("direction", DIRECTIONS.get(String.valueOf(luoShuNumber)));

        result.put("wuxing", wuxing);

        result.put("upperGuaNumber", upper);

        result.put("lowerGuaNumber", lower);

        result.put("movingYao", moving);

        return result;
    }
}