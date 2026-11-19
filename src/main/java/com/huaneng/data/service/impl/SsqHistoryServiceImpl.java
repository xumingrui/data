package com.huaneng.data.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huaneng.data.entity.SsqHistory;
import com.huaneng.data.mapper.SsqHistoryMapper;
import com.huaneng.data.service.SsqHistoryService;
import com.huaneng.data.util.DateUtil;
import com.huaneng.data.util.SsqLotteryServiceImpl;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xumingrui
 * @since 2026-11-04
 */
@Service
public class SsqHistoryServiceImpl extends ServiceImpl<SsqHistoryMapper, SsqHistory> implements SsqHistoryService {
    @Resource
    SsqHistoryMapper ssqHistoryMapper;

    @Override
    public int setSum(String num) {
        return ssqHistoryMapper.setSum(num);
    }

    @Override
    public int setAvage(String num) {
        return ssqHistoryMapper.setAvage(num);
    }

    @Override
    public JSONObject getByNum(String num) throws IOException {
        SsqLotteryServiceImpl service = new SsqLotteryServiceImpl();
        return service.fetchByIssue(num);
    }

    @Override
    public List<SsqHistory> countByNlDate(Integer year, Integer nlMonth, Integer nlDay) {
        return ssqHistoryMapper.countByNlDate(year, nlMonth, nlDay);
    }

    public SsqHistory updateByNum(String num) throws IOException {
        JSONObject ssq = this.getByNum(num);
        SsqHistory ssqHistory = new SsqHistory();
        ssqHistory.setNum(ssq.getString("num"));
        ssqHistory.setYldate(ssq.getString("yldate"));
        ssqHistory.setWeek(ssq.getString("week"));
        ssqHistory.setBlue(ssq.getString("blue"));
        ssqHistory.setSales(ssq.getLongValue("sales"));
        ssqHistory.setPoolmoney(ssq.getLongValue("poolmoney"));
        String red = ssq.getString("red");
        List<String> redList = Arrays.stream(red.split(",")).map(String::toString).collect(Collectors.toList());
        ssqHistory.setRed1(redList.get(0));
        ssqHistory.setRed2(redList.get(1));
        ssqHistory.setRed3(redList.get(2));
        ssqHistory.setRed4(redList.get(3));
        ssqHistory.setRed5(redList.get(4));
        ssqHistory.setRed6(redList.get(5));
        com.alibaba.fastjson.JSONObject nlDate = DateUtil.getLunarDate(ssq.getString("yldate"));
        ssqHistory.setNlyear(nlDate.getInteger("year"));
        ssqHistory.setNlmonth(nlDate.getInteger("month"));
        ssqHistory.setNlday(nlDate.getInteger("day"));
        ssqHistory.setDayGanzhi(nlDate.getString("ganzhiDay"));
        String nl = ssqHistory.getNlyear() + "-" + Math.abs(ssqHistory.getNlmonth()) + "-" + ssqHistory.getNlday();
        ssqHistory.setNldate(ssqHistory.getNlmonth() > 0 ? nl : "闰" + nl);
        ssqHistory.setCnnlyear(nlDate.getString("yearCn"));
        LocalDate yldate = DateUtil.stringTolocalDate(ssq.getString("yldate"));
        ssqHistory.setYldayYear(yldate.getDayOfYear());
        ssqHistory.setYlyear(yldate.getYear());
        ssqHistory.setYlmonth(yldate.getMonthValue());
        ssqHistory.setYlday(yldate.getDayOfMonth());
        long nldiff = DateUtil.getLunarDayOfYear(ssq.getString("yldate"));
        ssqHistory.setNldayYear(Integer.valueOf(String.valueOf(nldiff)));
        ssqHistory = getGongwei(ssqHistory);
        Boolean flag = this.saveOrUpdate(ssqHistory);
        if (flag) {
            SsqHistory sh = this.getById(ssqHistory.getNum());
            this.setSum(sh.getNum());
            this.setAvage(sh.getNum());
        }

        return this.getById(ssqHistory.getNum());
    }

    public SsqHistory getGongwei(SsqHistory hi) {
        String[] digits = {"离", "坎", "坤", "震", "巽", "中", "乾", "兑", "艮", "离"};

        int red1 = Integer.parseInt(hi.getRed1());
        int red2 = Integer.parseInt(hi.getRed2());
        int red3 = Integer.parseInt(hi.getRed3());
        int red4 = Integer.parseInt(hi.getRed4());
        int red5 = Integer.parseInt(hi.getRed5());
        int red6 = Integer.parseInt(hi.getRed6());
        int blue = Integer.parseInt(hi.getBlue());
        int mod1 = red1 % 9;
        int mod2 = red2 % 9;
        int mod3 = red3 % 9;
        int mod4 = red4 % 9;
        int mod5 = red5 % 9;
        int mod6 = red6 % 9;
        int mod = blue % 9;

        int[] nums = {mod1, mod2, mod3, mod4, mod5, mod6};
        Arrays.sort(nums);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            sb.append(digits[nums[i]] + " ");
        }
        hi.setRedGongwei(sb.toString());
        hi.setBlueGongwei(digits[mod]);
        return hi;
    }

    public Boolean isZhishu(Integer num) {
        Integer[] zhi = new Integer[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31};
        List<Integer> zhiList = Arrays.asList(zhi);
        if (zhiList.contains(num)) {
            return true;
        }
        return false;
    }

}
