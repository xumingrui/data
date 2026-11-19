package com.huaneng.data.util;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class YiXueUtils {

    /**
     * 根据阳历日期时间计算易学数据
     */
    public static Map<String, Object> calculate(String dateTime) {

        Map<String, Object> result = new LinkedHashMap<>();

        // 1. 梅花易数
        Map<String, Object> meiHua = MeiHuaUtils.calculate(dateTime);

        // 2. 生辰八字
        Map<String, Object> baZi = BaZiUtils.calculate(dateTime);

        // 3. 河图洛书
        Map<String, Object> luoShu = LuoShuUtils.calculate(dateTime);

        // 4. 易经数字
        Map<String, Object> yiJing = YiJingNumberUtils.calculate(dateTime);

        // 5. 六十四卦
        int upper = (Integer)meiHua.get("upperNumber");
        int lower = (Integer)meiHua.get("lowerNumber");
        int moving = (Integer)meiHua.get("movingYao");

        Map<String, Object> gua = GuaUtils.calculate(upper, lower, moving);

        // 6. 每日幸运数据
        int luoShuNumber = (Integer)luoShu.get("luoShuNumber");

        // 这里暂时使用洛书数字对应的五行
        String wuxing = (String)luoShu.get("wuxing");

        Map<String, Object> lucky = LuckyUtils.calculate(upper, lower, moving, luoShuNumber, wuxing);

        result.put("meiHua", meiHua);
        result.put("baZi", baZi);
        result.put("luoShu", luoShu);
        result.put("yiJing", yiJing);
        result.put("gua", gua);
        result.put("lucky", lucky);

        return result;
    }

    public static void main(String[] args) {
        Map<String, Object> result = YiXueUtils.calculate("2024-10-27 21:30:00");
        System.out.println(JSONObject.toJSONString(result));

    }
}