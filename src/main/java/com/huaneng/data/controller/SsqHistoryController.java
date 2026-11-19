package com.huaneng.data.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.annotation.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huaneng.common.result.CommonResult;
import com.huaneng.data.entity.SsqHistory;
import com.huaneng.data.service.impl.SsqHistoryServiceImpl;
import com.huaneng.data.util.DateUtil;
import com.huaneng.data.util.FillCheckMarkOnTemplate;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 控制器
 * </p>
 *
 * @author xumingrui
 * @since 2026-11-04
 */
@RestController

@Slf4j
@Api(tags = "ssq_historyCRUD接口")
@RequestMapping("/data/ssq_history")
public class SsqHistoryController {

    @Resource
    private SsqHistoryServiceImpl ssqHistoryService;

    @ApiOperation(value = "init before 2013", notes = "")
    @PostMapping("/initHistory")
    public CommonResult initHistory() throws IOException {
        LambdaQueryWrapper<SsqHistory> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.lt(SsqHistory::getYlyear, 2013);
        List<SsqHistory> list = ssqHistoryService.list(lambdaQuery);
        JSONObject result = new JSONObject();
        int sucess = 0;
        StringBuffer sb = new StringBuffer();
        for (SsqHistory ssqHistory : list) {
            try {
                // 获取农历日期
                com.alibaba.fastjson.JSONObject nlDate = DateUtil.getLunarDate(ssqHistory.getYldate());
                ssqHistory.setNlyear(nlDate.getInteger("year"));
                ssqHistory.setNlmonth(nlDate.getInteger("month"));
                ssqHistory.setNlday(nlDate.getInteger("day"));
                ssqHistory.setDayGanzhi(nlDate.getString("ganzhiDay"));
                ssqHistory.setNldate(ssqHistory.getNlmonth() > 0 ? "" : "闰" + ssqHistory.getNlyear() + "-"
                    + Math.abs(ssqHistory.getNlmonth()) + "-" + ssqHistory.getNlday());
                ssqHistory.setCnnlyear(nlDate.getString("yearCn"));
                // 设置阳历日期
                LocalDate yldate = DateUtil.stringTolocalDate(ssqHistory.getYldate());
                int i = yldate.getDayOfWeek().getValue();
                String[] digits = {"零", "一", "二", "三", "四", "五", "六", "日"};
                ssqHistory.setWeek(digits[i]);
                ssqHistory.setYldayYear(yldate.getDayOfYear());
                ssqHistory.setYlyear(yldate.getYear());
                ssqHistory.setYlmonth(yldate.getMonthValue());
                ssqHistory.setYlday(yldate.getDayOfMonth());
                //
                long nldiff = DateUtil.getLunarDayOfYear(ssqHistory.getYldate());
                ssqHistory.setNldayYear(Integer.valueOf(String.valueOf(nldiff)));
                Boolean flag = ssqHistoryService.saveOrUpdate(ssqHistory);
                if (flag) {
                    SsqHistory sh = ssqHistoryService.getById(ssqHistory.getNum());
                    ssqHistoryService.setSum(sh.getNum());
                    ssqHistoryService.setAvage(sh.getNum());
                }
                sucess++;
            } catch (Exception e) {
                sb.append("," + ssqHistory.getNum());
            }
        }
        result.put("total", list.size());
        result.put("sucess", sucess);
        result.put("fail", sb.toString());
        return CommonResult.success(result);
    }

    @ApiOperation(value = "init all", notes = "")
    @PostMapping("/list")
    public CommonResult list() throws IOException {
        List<SsqHistory> list = ssqHistoryService.list();
        int sucess = 0;
        StringBuffer sb = new StringBuffer();
        for (SsqHistory hi : list) {
            try {
                ssqHistoryService.updateByNum(hi.getNum());
                sucess++;
            } catch (Exception e) {
                sb.append("," + hi.getNum());
                log.error("Error   num=" + hi.getNum());
                e.printStackTrace();
            }
        }
        JSONObject result = new JSONObject();
        result.put("total", list.size());
        result.put("sucess", sucess);
        result.put("fail", sb.toString());
        return CommonResult.success(result);
    }

    @ApiOperation(value = "修改ssq_history", notes = "根据id修改ssq_history")
    @PostMapping("/update")
    public CommonResult update(@RequestBody SsqHistory ssqHistory) {
        Boolean flag = ssqHistoryService.updateById(ssqHistory);
        return CommonResult.success(flag);
    }

    @ApiOperation(value = "添加ssq_history", notes = "新增一条ssq_history")
    @PostMapping("/add")
    public CommonResult add(@RequestParam("num") String num) throws IOException {
        SsqHistory ssqHistory = ssqHistoryService.updateByNum(num);
        return CommonResult.success(ssqHistory);
    }

    @ApiOperation(value = "根据日期统计数据出现次数", notes = "根据日期统计数据出现次数")
    @PostMapping("/counts")
    public CommonResult counts(@RequestParam("nldate") String nldate) throws IOException {

        LocalDate date = DateUtil.stringTolocalDate(nldate);
        int monthValue = date.getMonthValue();
        int dayOfMonth = date.getDayOfMonth();
        int year = date.getYear();
        List<SsqHistory> list = ssqHistoryService.countByNlDate(year, monthValue, dayOfMonth);

        Map<String, Integer> map = initMap();
        JSONObject result = new JSONObject();
        for (SsqHistory data : list) {
            map.put(data.getRed1(), map.get(data.getRed1()) + 1);
            map.put(data.getRed2(), map.get(data.getRed2()) + 1);
            map.put(data.getRed3(), map.get(data.getRed3()) + 1);
            map.put(data.getRed4(), map.get(data.getRed4()) + 1);
            map.put(data.getRed5(), map.get(data.getRed5()) + 1);
            map.put(data.getRed6(), map.get(data.getRed6()) + 1);
        }
        result.put("size", list.size());
        result.put("data", com.alibaba.fastjson.JSONObject.toJSONString(map));
        return CommonResult.success(result);
    }

    public Map<String, Integer> initMap() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (int i = 1; i < 34; i++) {
            if (i < 10) {
                result.put("0" + i, 0);
            } else {
                result.put("" + i, 0);
            }
        }
        return result;

    }

    /**
     * @param type
     * @param param
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "导出数据 按农历、阳历、期号、日干支", notes = "导出数据  按农历、阳历、期号、日干支")
    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> download(
        @RequestParam(value = "type", defaultValue = "nl,yl,num、riganzhi") String type,
        @RequestParam(value = "param", required = true) String param) throws IOException {
        List<SsqHistory> dataList;
        LambdaQueryWrapper<SsqHistory> lambdaQuery = Wrappers.lambdaQuery();
        String t = null;
        if (StringUtils.isBlank(type)) {
            type = "nl";
            t = "(农历)";
        }
        if (type.equals("nl") || type.equals("yl")) {
            LocalDate date = DateUtil.stringTolocalDate(param);
            Integer monthValue = date.getMonthValue();
            int dayOfMonth = date.getDayOfMonth();
            int year = date.getYear();

            if (type.equals("nl")) {
                t = "(农历)";
                lambdaQuery.apply("ABS(nlmonth) = {0}", monthValue);
                lambdaQuery.eq(SsqHistory::getNlday, dayOfMonth);
                lambdaQuery.le(SsqHistory::getNlyear, year);
            }
            if (type.equals("yl")) {
                t = "(阳历)";
                lambdaQuery.eq(SsqHistory::getYlmonth, monthValue);
                lambdaQuery.eq(SsqHistory::getYlday, dayOfMonth);
                lambdaQuery.le(SsqHistory::getYlyear, year);
            }

        } else if (type.equals("num")) {
            t = "(期号)";
            lambdaQuery.likeLeft(SsqHistory::getNum, param);
        } else if (type.equals("riganzhi")) {
            t = "(干支)";
            lambdaQuery.eq(SsqHistory::getDayGanzhi, param);
            lambdaQuery.orderByAsc(SsqHistory::getNum);
        }

        dataList = ssqHistoryService.list(lambdaQuery);
        String d = t;
        // 中文文件名编码：URLEncoder + 把 + 替换成 %20
        String fileName = URLEncoder.encode("result_" + System.currentTimeMillis() + ".xlsx", "UTF-8");
        StreamingResponseBody body = outputStream -> {
            // 把 fillCheckMark 改成写入 outputStream 而非 ByteArrayOutputStream
            try {
                FillCheckMarkOnTemplate.fillData(dataList, d, outputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName)
            .contentType(MediaType.APPLICATION_OCTET_STREAM).body(body);
    }

    @ApiOperation(value = "初始化日天干", notes = "初始化日天干")
    @PostMapping("/initDayGanzhi")
    public CommonResult initDayGanzhi(@RequestParam(value = "yldate", required = false) String yldate) {
        LambdaQueryWrapper<SsqHistory> lambdaQuery = Wrappers.lambdaQuery();
        List<SsqHistory> list = new ArrayList<>();
        if (StringUtils.isNotBlank(yldate)) {
            lambdaQuery.eq(SsqHistory::getYldate, yldate);
            list = ssqHistoryService.list(lambdaQuery);
        } else {
            list = ssqHistoryService.list();
        }
        LambdaUpdateWrapper<SsqHistory> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        for (SsqHistory hi : list) {
            com.alibaba.fastjson.JSONObject lunarDate = DateUtil.getLunarDate(hi.getYldate());
            lambdaUpdateWrapper.clear();;
            lambdaUpdateWrapper.eq(SsqHistory::getNum, hi.getNum());
            lambdaUpdateWrapper.set(SsqHistory::getDayGanzhi, lunarDate.getString("ganzhiDay"));
            ssqHistoryService.update(lambdaUpdateWrapper);
        }
        return CommonResult.success(list.size());

    }

    @ApiOperation(value = "初始化球所属宫位", notes = "初始化球所属宫位")
    @PostMapping("/initGongWei")
    public CommonResult initGongWei(@RequestParam(value = "num", required = false) String num) {
        LambdaQueryWrapper<SsqHistory> lambdaQuery = Wrappers.lambdaQuery();
        List<SsqHistory> list = new ArrayList<>();
        if (StringUtils.isNotBlank(num)) {
            lambdaQuery.eq(SsqHistory::getNum, num);
            list = ssqHistoryService.list(lambdaQuery);
        } else {
            list = ssqHistoryService.list();
        }
        LambdaUpdateWrapper<SsqHistory> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        for (SsqHistory hi : list) {
            String[] digits = {"坎", "坤", "震", "巽", "中", "乾", "兑", "艮", "离"};
            Map<String, String> wuxing = new HashMap<>();
            wuxing.put("坎", "水");
            wuxing.put("坎", "水");
            wuxing.put("坎", "水");
            wuxing.put("坎", "水");
            wuxing.put("坎", "水");
            wuxing.put("坎", "水");
            wuxing.put("坎", "水");
            wuxing.put("坎", "水");
            wuxing.put("坎", "水");

            lambdaUpdateWrapper.clear();
            int red1 = Integer.parseInt(hi.getRed1());
            int red2 = Integer.parseInt(hi.getRed2());
            int red3 = Integer.parseInt(hi.getRed3());
            int red4 = Integer.parseInt(hi.getRed4());
            int red5 = Integer.parseInt(hi.getRed5());
            int red6 = Integer.parseInt(hi.getRed6());
            int blue = Integer.parseInt(hi.getBlue());
            int mod1 = (red1 % 9 == 0 ? 9 : red1 % 9) - 1;
            int mod2 = (red2 % 9 == 0 ? 9 : red2 % 9) - 1;
            int mod3 = (red3 % 9 == 0 ? 9 : red3 % 9) - 1;
            int mod4 = (red4 % 9 == 0 ? 9 : red4 % 9) - 1;
            int mod5 = (red5 % 9 == 0 ? 9 : red5 % 9) - 1;
            int mod6 = (red6 % 9 == 0 ? 9 : red6 % 9) - 1;
            int mod = (blue % 9 == 0 ? 9 : blue % 9) - 1;
            String redGongwei = digits[mod1] + DateUtil.getWuxing(digits[mod1]) + " " + digits[mod2]
                + DateUtil.getWuxing(digits[mod2]) + " " + digits[mod3] + DateUtil.getWuxing(digits[mod3]) + " "
                + digits[mod4] + DateUtil.getWuxing(digits[mod4]) + " " + digits[mod5]
                + DateUtil.getWuxing(digits[mod5]) + " " + digits[mod6] + DateUtil.getWuxing(digits[mod6]);

            hi.setRedGongwei(redGongwei);
            hi.setBlueGongwei(digits[mod] + DateUtil.getWuxing(digits[mod]));
            lambdaUpdateWrapper.eq(SsqHistory::getNum, hi.getNum());
            lambdaUpdateWrapper.set(SsqHistory::getRedGongwei, hi.getRedGongwei());
            lambdaUpdateWrapper.set(SsqHistory::getBlueGongwei, hi.getBlueGongwei());
            ssqHistoryService.update(lambdaUpdateWrapper);
        }
        return CommonResult.success(list.size());

    }

    @ApiOperation(value = "初始化质/合、奇/偶", notes = "初始化质/合、奇/偶")
    @PostMapping("/initZhiheAndJiou")
    public CommonResult initZhiheAndJiou(@RequestParam(value = "num", required = false) String num) {
        LambdaQueryWrapper<SsqHistory> lambdaQuery = Wrappers.lambdaQuery();
        List<SsqHistory> list;
        if (StringUtils.isNotBlank(num)) {
            lambdaQuery.eq(SsqHistory::getNum, num);
        }
        list = ssqHistoryService.list(lambdaQuery);
        for (SsqHistory hi : list) {
            int zhiCount = 0;
            int jiCount = 0;
            List<Integer> redList = new ArrayList<>();
            redList.add(Integer.parseInt(hi.getRed1()));
            redList.add(Integer.parseInt(hi.getRed2()));
            redList.add(Integer.parseInt(hi.getRed3()));
            redList.add(Integer.parseInt(hi.getRed4()));
            redList.add(Integer.parseInt(hi.getRed5()));
            redList.add(Integer.parseInt(hi.getRed6()));
            for (Integer red : redList) {
                if (ssqHistoryService.isZhishu(red)) {
                    zhiCount++;
                }
                if (red % 2 == 1) {
                    jiCount++;
                }
            }
            hi.setJiouBi(jiCount + "奇 :" + (6 - jiCount) + " 偶");
            hi.setZhiheBi(zhiCount + "质 :" + (6 - zhiCount) + " 合");

            ssqHistoryService.updateById(hi);
        }
        return CommonResult.success("成功");
    }

    @ApiOperation(value = "初始化指定年份的开奖日期", notes = "初始化指定年份的开奖日期")
    @PostMapping("/initDay")
    public CommonResult initDay(@RequestParam(value = "year", required = false) Integer year) {
        if (year == null) {
            year = LocalDate.now().getYear();
        }
        // 获取指定年份的最大数
        LambdaQueryWrapper<SsqHistory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 指定查询字段为最大值，并起别名方便获取
        lambdaQueryWrapper.eq(SsqHistory::getYlyear, year);
        lambdaQueryWrapper.orderByDesc(SsqHistory::getNum);
        lambdaQueryWrapper.last("limit 1");
        SsqHistory his = ssqHistoryService.getOne(lambdaQueryWrapper);

        // 使用 selectObj 获取单个结果对象
        LocalDate startDate = null;
        String num = "";
        if (his == null) {
            startDate = LocalDate.of(year, 1, 1);
            num = year + "001";
            // startDate = firstDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            startDate = DateUtil.stringTolocalDate(his.getYldate()).plusDays(1);
            num = getNextNum(his.getNum());
        }
        for (int day = 0; day <= 500; day++) {
            startDate = startDate.plusDays(day);
            // 判断星期几
            int week = startDate.getDayOfWeek().getValue();
            if (week == 2 || week == 4 || week == 7) {
                SsqHistory ssqHistory = new SsqHistory();
                ssqHistory.setNum(num);
                ssqHistory.setYldate(startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                // ssqHistory.setWeek(week==2?"二");
            }
            if (startDate.getMonthValue() == 12 && startDate.getDayOfMonth() == 31) {

            }

        }

        return CommonResult.success(startDate);

    }

    public String getNextNum(String preNum) {
        String year = preNum.substring(0, 4);
        String lastnum = preNum.substring(4, 7);
        String result = "";
        Integer num = Integer.parseInt(lastnum) + 1;
        if (num < 10) {
            result = year + "00" + num;
        } else if (num < 100) {
            result = year + "0" + num;
        } else {
            result = year + num;
        }
        return result;
    }

    public String getWeek(Integer week) {
        switch (week) {
            case 2:
                return "二";
            case 4:
                return "四";
            case 7:
                return "日";
            default:
                return "失败";
        }

    }

    public static void main(String[] args) {
        String preNum = "2026009";
        String year = preNum.substring(0, 4);
        String lastnum = preNum.substring(4, 7);
        String result = "";
        Integer num = Integer.parseInt(lastnum) + 1;
        if (num < 10) {
            result = year + "00" + num;
        } else if (num < 100) {
            result = year + "0" + num;
        } else {
            result = year + num;
        }
        System.out.println(result);
    }
}
