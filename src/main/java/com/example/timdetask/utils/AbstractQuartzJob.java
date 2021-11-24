package com.example.timdetask.utils;


import com.example.timdetask.constants.Constants;
import com.example.timdetask.constants.ScheduleConstants;
import com.example.timdetask.domain.SysJob;
import com.example.timdetask.domain.SysJobLog;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抽象quartz调用
 *
 * @author
 */
public abstract class AbstractQuartzJob implements Job
{
    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        SysJob sysJob = new SysJob();
        BeanUtils.copyBeanProp(sysJob, context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES));
        try
        {
            // 执行前方法
            // 新建一个线程局部变量，设置相关值（主要是时间设置）
            before(context, sysJob);
            if (sysJob != null)
            {
                // 如果任务里面有内容，就执行这个任务
                // 任务有 允许并发执行/不允许并发执行 两种
                // 是否允许是通过 @DisallowConcurrentExecution实现的
                doExecute(context, sysJob);
            }
            // 执行后方法
            // 主要是记录日志（成功信息或者失败信息）
            // 再通过SpringUtil获取到service，将日志add到数据库中
            // 这里注释掉入库了
            after(context, sysJob, null);
        }
        catch (Exception e)
        {
            log.error("任务执行异常  - ：", e);
            after(context, sysJob, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob 系统计划任务
     */
    protected void before(JobExecutionContext context, SysJob sysJob)
    {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param
     */
    protected void after(JobExecutionContext context, SysJob sysJob, Exception e)
    {
        // 通过之前的线程局部变量获取到起始时间
        Date startTime = threadLocal.get();
        // 删除此线程局部变量
        threadLocal.remove();
        // 记录任务日志
        final SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobName(sysJob.getTaskName());
        sysJobLog.setJobGroup(sysJob.getJobGroup());
        sysJobLog.setInvokeTarget(sysJob.getTaskClassName());
        sysJobLog.setStartTime(startTime);
        sysJobLog.setEndTime(new Date());
        // 计算运行时间
        long runMs = sysJobLog.getEndTime().getTime() - sysJobLog.getStartTime().getTime();
        sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null)
        {
            // 存在异常，记录异常信息
            // 设置任务失败标志
            sysJobLog.setStatus(Constants.FAIL);
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            sysJobLog.setExceptionInfo(errorMsg);
        }
        else
        {
            // 设置任务成功标志
            sysJobLog.setStatus(Constants.SUCCESS);
        }

        // 写入数据库当中
        //SpringUtils.getBean(ISysJobLogService.class).addJobLog(sysJobLog);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob 系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception;
}
