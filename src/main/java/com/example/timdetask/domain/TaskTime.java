package com.example.timdetask.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 定时发起时间对象
 */
@Data
public class TaskTime implements Serializable {
    /**
     * 是否为每天
     */
    private Boolean isEveryDay;
    /**
     * 是否为每周
     */
    private Boolean isWeek;
    /**
     * 是否为每月
     */
    private Boolean isMonth;
    /**
     * 时
     */
    private Integer time;
    /**
     * 分
     */
    private Integer minute;
    /**
     * 周几
     */
    private Integer weekNumber;
    /**
     * 几号
     */
    private Integer day;

}
