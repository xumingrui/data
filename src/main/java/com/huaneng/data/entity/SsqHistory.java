package com.huaneng.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author xumingrui
 * @since 2026-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ssq_history")
@ApiModel(value = "SsqHistory对象", description = "")
public class SsqHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("num")
    private String num;

    @TableField("yldate")
    private String yldate;

    @TableField("nldate")
    private String nldate;

    @TableField("ylyear")
    private Integer ylyear;

    @TableField("red1")
    private String red1;

    @TableField("red2")
    private String red2;

    @TableField("red3")
    private String red3;

    @TableField("red4")
    private String red4;

    @TableField("red5")
    private String red5;

    @TableField("red6")
    private String red6;

    @TableField("blue")
    private String blue;

    @TableField("total_sum_value")
    private Integer totalSumValue;

    @TableField("total_avage_value")
    private BigDecimal totalAvageValue;

    @TableField("red_sum_value")
    private Integer redSumValue;

    @TableField("red_avage_value")
    private BigDecimal redAvageValue;

    @TableField("ylmonth")
    private Integer ylmonth;

    @TableField("ylday")
    private Integer ylday;

    @TableField("nlyear")
    private Integer nlyear;

    @TableField("nlmonth")
    private Integer nlmonth;

    @TableField("nlday")
    private Integer nlday;

    @TableField("cnnlyear")
    private String cnnlyear;

    @TableField("sales")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sales;

    @TableField("poolmoney")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long poolmoney;

    @TableField("week")
    private String week;

    @TableField("nlday_year")
    private Integer nldayYear;

    @TableField("ylday_year")
    private Integer yldayYear;

    @TableField("day_ganzhi")
    private String dayGanzhi;

    @TableField("red_gongwei")
    private String redGongwei;

    @TableField("blue_gongwei")
    private String blueGongwei;

    @TableField("zhihe_bi")
    private String zhiheBi;

    @TableField("jiou_bi")
    private String jiouBi;

}