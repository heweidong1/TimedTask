package com.example.timdetask.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TaskListResultVo implements Serializable {
    /**
     * 业务id
     */
    private Long busiId;
    /**
     * 定时任务组id
     */
    private String taskGroupId;

    /**
     * 办公类别
     */
    private String officeTypeName;
    /**
     * 业务类型
     *
     */
    private String busiTypeName;
    /**
     * 定时任务状态
     */
    private Integer taskStatus;
    /**
     * 业务名称
     */
    private String busiName;
    /**
     * 标题
     */
    private String title;
    /**
     * 开始时间pc端
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 办公类别
     */
    private String officeType;
    /**
     * 业务类型
     */
    private String busiType;
    /**
     * 格式化开始时间移动端
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date initialTime;
    /**
     * 格式化完成时间移动端
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date finishTime;



}
