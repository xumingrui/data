package com.huaneng.data.service;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huaneng.data.entity.SsqHistory;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author xumingrui
 * @since 2026-11-04
 */
public interface SsqHistoryService extends IService<SsqHistory> {
    int setSum(String num);

    int setAvage(String num);

    JSONObject getByNum(String num) throws IOException;

    List<SsqHistory> countByNlDate(Integer year, Integer nlMonth, Integer nlDay);
}
