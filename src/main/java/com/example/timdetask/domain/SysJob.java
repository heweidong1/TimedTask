package com.example.timdetask.domain;


import com.example.timdetask.constants.ScheduleConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务调度表 sysTask
 * 
 * @author
 */
@Data
public class SysJob  implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long Id;

    /** 任务名称 */
    private String taskName;

    /** 任务组名 */
    private String jobGroup;

    /** 调用目标字符串 */
    @NotBlank(message = "调用目标字符串不允许为空")
    private String taskClassName;

    /** cron执行表达式 */
    @NotBlank(message = "周期表达式不允许为空")
    private String taskCorn;

    /** cron计划策略 */
   //"0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行"
    private String misfirePolicy = ScheduleConstants.MISFIRE_DEFAULT;

    /** 是否并发执行（0允许 1禁止） */
    private String concurrent;

    /** 任务状态（0正常 1暂停） */
    private String taskFlag;

    /**任务描述**/
    private String taskDescription;

    /**
     * 定时任务所属系统
     */
    private Integer taskSystemId;
    /**
     * 所属系统名称
     */
    private String taskSystemName;

    /**
     * 预留字段1(用作参数)
     */
    private String remake1;

    /**
     * 定时任务组id
     */
    private String taskGroupId;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;




    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    /**删除标识*/
    private Integer deleteFlag;




}