package com.huaneng.data.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class GuaUtils {

    private static final String[] GUA = {"乾", "兑", "离", "震", "巽", "坎", "艮", "坤"};

    // 八卦二进制编码，自下而上
    private static final int[] GUA_CODE = {7, 6, 5, 4, 3, 2, 1, 0};

    // 六十四卦名称
    private static final String[][] GUA_NAMES = {

        {"乾为天", "天泽履", "天火同人", "天雷无妄", "天风姤", "天水讼", "天山遁", "天地否"},

        {"泽天夬", "兑为泽", "泽火革", "泽雷随", "泽风大过", "泽水困", "泽山咸", "泽地萃"},

        {"火天大有", "火泽睽", "离为火", "火雷噬嗑", "火风鼎", "火水未济", "火山旅", "火地晋"},

        {"雷天大壮", "雷泽归妹", "雷火丰", "震为雷", "雷风恒", "雷水解", "雷山小过", "雷地豫"},

        {"风天小畜", "风泽中孚", "风火家人", "风雷益", "巽为风", "风水涣", "风山渐", "风地观"},

        {"水天需", "水泽节", "水火既济", "水雷屯", "水风井", "坎为水", "水山蹇", "水地比"},

        {"山天大畜", "山泽损", "山火贲", "山雷颐", "山风蛊", "山水蒙", "艮为山", "山地剥"},

        {"地天泰", "地泽临", "地火明夷", "地雷复", "地风升", "地水师", "地山谦", "坤为地"}};

    /**
     * 根据上下卦获取卦名
     */
    public static String getGuaName(int upper, int lower) {
        return GUA_NAMES[upper - 1][lower - 1];
    }

    /**
     * 根据三个爻计算八卦 bits: 自下而上
     */
    public static int getGuaNumber(int bits) {

        for (int i = 0; i < GUA_CODE.length; i++) {
            if (GUA_CODE[i] == bits) {
                return i + 1;
            }
        }

        throw new IllegalArgumentException("无效卦象");
    }

    public static String getGua(int number) {
        return GUA[number - 1];
    }

    /**
     * 计算本卦、互卦、变卦
     *
     * lines: 六爻数组，自下而上
     */
    public static Map<String, Object> calculate(int upper, int lower, int movingYao) {

        // 原始六爻
        int[] lines = new int[6];

        int lowerCode = GUA_CODE[lower - 1];
        int upperCode = GUA_CODE[upper - 1];

        lines[0] = lowerCode & 1;
        lines[1] = (lowerCode >> 1) & 1;
        lines[2] = (lowerCode >> 2) & 1;

        lines[3] = upperCode & 1;
        lines[4] = (upperCode >> 1) & 1;
        lines[5] = (upperCode >> 2) & 1;

        // 计算互卦
        // 下互卦：二、三、四爻
        int mutualLower = lines[1] | (lines[2] << 1) | (lines[3] << 2);

        // 上互卦：三、四、五爻
        int mutualUpper = lines[2] | (lines[3] << 1) | (lines[4] << 2);

        int mutualLowerNumber = getGuaNumber(mutualLower);

        int mutualUpperNumber = getGuaNumber(mutualUpper);

        // 计算变卦
        int[] changedLines = lines.clone();

        int index = movingYao - 1;

        // 阴阳互变
        changedLines[index] = 1 - changedLines[index];

        int changedLower = changedLines[0] | (changedLines[1] << 1) | (changedLines[2] << 2);

        int changedUpper = changedLines[3] | (changedLines[4] << 1) | (changedLines[5] << 2);

        int changedLowerNumber = getGuaNumber(changedLower);

        int changedUpperNumber = getGuaNumber(changedUpper);

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("originalGua", getGuaName(upper, lower));

        result.put("mutualGua", getGuaName(mutualUpperNumber, mutualLowerNumber));

        result.put("changedGua", getGuaName(changedUpperNumber, changedLowerNumber));

        result.put("movingYao", movingYao);

        return result;
    }
}