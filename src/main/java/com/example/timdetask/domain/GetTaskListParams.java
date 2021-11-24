package com.example.timdetask.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 定时任务列表筛选对象
 */
@Data
public class GetTaskListParams implements Serializable {

    private List<String> busiTypes;

    private List<String> officeTypes;

    private String find;

    private Integer taskStatus;

    private Integer pageSize;

    private Integer pageNum;
}
