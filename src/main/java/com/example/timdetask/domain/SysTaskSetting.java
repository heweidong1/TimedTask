package com.example.timdetask.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务与gic_busi_setting关联表(SysTaskSetting)实体类
 *
 * @author makejava
 * @since 2021-08-24 22:46:02
 */
@Data
public class SysTaskSetting implements Serializable {
    private static final long serialVersionUID = 517027027257227111L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 定时任务id
     */
    private Integer taskId;
    /**
     * gbsID
     */
    private Long busiSettingId;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改人
     */
    private String updateBy;
    /**
     * 修改时间
     */
    private Date updateTime;


}
