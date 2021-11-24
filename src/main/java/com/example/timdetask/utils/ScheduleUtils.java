package com.example.timdetask.utils;


import com.example.timdetask.constants.ScheduleConstants;
import com.example.timdetask.domain.SysJob;
import com.example.timdetask.exception.TaskException;
import org.quartz.*;

/**
 * 定时任务工具类
 *  Quartz API的关键接口是：
 *        Scheduler - 与调度程序交互的主要API。
 *        Job - 由希望由调度程序执行的组件实现的接口。
 *        JobDetail - 用于定义作业的实例。
 *        Trigger（即触发器） - 定义执行给定作业的计划的组件。
 *        JobBuilder - 用于定义/构建JobDetail实例，用于定义作业的实例。
 *        TriggerBuilder - 用于定义/构建触发器实例。
 * 
 * @author
 *
 */
public class ScheduleUtils
{
    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(SysJob sysJob)
    {
        // 查看任务是否允许并发执行
        boolean isConcurrent = "0".equals(sysJob.getConcurrent());
         // 根据是否允许并发分别得到对应的Class
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 构建任务触发对象
     * 进行字符串拼接（拼接前缀）
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup)
    {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup)
    {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, SysJob job) throws SchedulerException, TaskException
    {
        // 得到quartz任务类的Class
        // quartz任务类继承了AbstractQuartzJob类，而该类实现了Job接口
        Class<? extends Job> jobClass = getQuartzJobClass(job);
        // 构建job信息
        Long jobId = job.getId();
        String jobGroup = job.getJobGroup();
        // 使用JobBuilder根据quartz任务类的Class使用静态方法newJob去build一个JobDetail的实例
        // 这个实例就可以去执行quartz任务类的相关方法（定义了一个Job作业）
        // withIdentity设置后，就可以在使用时使用其name和group作为Trigger去触发
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();

        // 表达式调度构建器
        // 表达式调度构建器，作为任务调度的容器
        // 使用cron表达式
        // 将sys_task表中任务所携带的cron表达式字段赋给Builder
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getTaskCorn());
        // 设置定时任务策略(cron计划策略)
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);

        // 按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                .withSchedule(cronScheduleBuilder).build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job);

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId, jobGroup)))
        {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
        }

        scheduler.scheduleJob(jobDetail, trigger);

        // 暂停任务
        if (job.getTaskFlag().equals(ScheduleConstants.Status.PAUSE.getValue()))
        {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysJob job, CronScheduleBuilder cb)
            throws TaskException
    {
        // 判断任务的执行策略(cron计划策略)
        // 具体请查看ScheduleConstants
        switch (job.getMisfirePolicy())
        {
            // 默认执行策略
            case ScheduleConstants.MISFIRE_DEFAULT:
                return cb;
            // 立即触发执行
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            // 触发一次执行
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
                return cb.withMisfireHandlingInstructionFireAndProceed();
            // 不触发立即执行
            case ScheduleConstants.MISFIRE_DO_NOTHING:
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                throw new TaskException("The task misfire policy '" + job.getMisfirePolicy()
                        + "' cannot be used in cron schedule tasks", TaskException.Code.CONFIG_ERROR);
        }
    }
}