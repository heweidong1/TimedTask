package com.example.timdetask.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定时发起参数
 */
@Getter
@Setter
@ToString
public class LaunchOnTimeParams {

    /** 附件相关信息量 */
    private ArrayList<Map> fileInfos;

    /** 表单整体jaon串 */
//    @ApiModelProperty(value="表单json串", required = true)
    private String formValues;

    /** 从草稿箱发起 则传入id*/
//    @ApiModelProperty(value="草稿ID，若是从草稿箱发起，则不为null",name="draftId",example="1215")
    private Long draftId;

    //业务表
//    private GicBusiSetting gicBusiSetting;

    /** 到达节点后截止填报的时长数字 */
//    @ApiModelProperty(value="到达节点后截止填报的时长数字",name="deadlinePeriod",example="3")
    private Integer deadlinePeriod;

    /** 到达节点后截止填报的时长单位（ "minutes","hour" ,"day"） */
    private String deadlineUnit;

    /** 截止时间前自动提醒的时长数字 */
    private Integer remindPeriod;

    /** 截止时间前自动提醒的时间单位（ "minutes","hour" ,"day"） */
    private String remindUnit;

    /** 截止时间前自动提醒方式：是否发送邮件提醒（是：true/否：false) */
    private String isEmail;

    /** 截止时间前自动提醒方式：是否发送短信提醒（是：true/否：false) */
    private String isMessage;

    /** 说明信息 */
    private String remark;
    /** 发起人自选信息 */
    List<Map<String,Map<String, List<String>>>> optionalNames;

    /** 发起人自选信息Json 用于发起页存草稿保存发起人信息 */
    private String assigneeJson;
    /**
     * 定时任务状态0开启，1关闭
     */
    private Integer taskStatus;

    /**
     * 定时任务时间配置
     */
    private List<TaskTime> taskTimes;
    /**
     * 登陆人信息
     */
//    private LoginUser loginUser;
    /**
     * 创建人名字
     */
    private String nickName;

}
