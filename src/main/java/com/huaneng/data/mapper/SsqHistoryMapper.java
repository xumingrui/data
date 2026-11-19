package com.huaneng.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huaneng.data.entity.SsqHistory;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xumingrui
 * @since 2026-11-04
 */
public interface SsqHistoryMapper extends BaseMapper<SsqHistory> {
    int setSum(@Param("num") String num);

    int setAvage(@Param("num") String num);

    List<SsqHistory> countByNlDate(@Param("year") Integer year, @Param("nlmonth") Integer nlmonth,
        @Param("nlday") Integer nlday);
}
