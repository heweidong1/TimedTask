package com.example.timdetask.controller;


import com.example.timdetask.domain.SysJob;
import com.example.timdetask.exception.TaskException;
import com.example.timdetask.service.ISysJobService;
import com.example.timdetask.utils.AjaxResult;
import com.example.timdetask.utils.CronUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 定时任务控制层
 * 
 * @author  zwz
 */
@Controller
@RequestMapping("/monitor/job")
public class SysJobController
{
    @Autowired
    private ISysJobService jobService;

    /**
     *
     * @param job 定时任务
     * @return
     */
    @PostMapping("/list")
    @ResponseBody
    public AjaxResult list(@RequestBody SysJob job)
    {
        List<SysJob> list = jobService.selectJobList(job);
        return AjaxResult.success(list);
    }


    /**
     *
     * @param ids 需要删除的数据IDs
     * @return
     * @throws SchedulerException
     */
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) throws SchedulerException
    {
        jobService.deleteJobByIds(ids);
        return AjaxResult.success();
    }


    /**
     * 任务调度状态修改
     * @param job 定时任务信息
     */
    @PostMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(@RequestBody SysJob job) throws SchedulerException
    {
        SysJob newJob = jobService.selectJobById(job.getId());
        newJob.setTaskFlag(job.getTaskFlag());
        return AjaxResult.success(jobService.changeStatus(newJob));
    }

    /**
     * 任务调度立即执行一次
     * @param job  定时任务信息
     */
    @PostMapping("/run")
    @ResponseBody
    public AjaxResult run(@RequestBody SysJob job) throws SchedulerException
    {
        jobService.run(job);
        return  AjaxResult.success();
    }


    /**
     * 新增保存调度
     * @param job  定时任务信息
     */
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated @RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getTaskCorn()))
        {
            return AjaxResult.error("cron表达式不正确");
        }
        return AjaxResult.success(jobService.insertJob(job));
    }


    /**
     * 修改保存调度
     * @param job 定时任务信息
     */
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated @RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getTaskCorn()))
        {
            return AjaxResult.error("cron表达式不正确");
        }
        return AjaxResult.success(jobService.updateJob(job));
    }

    /**
     * 校验cron表达式是否有效
     */
    @PostMapping("/checkCronExpressionIsValid")
    @ResponseBody
    public boolean checkCronExpressionIsValid(SysJob job)
    {
        return jobService.checkCronExpressionIsValid(job.getTaskCorn());
    }
}
