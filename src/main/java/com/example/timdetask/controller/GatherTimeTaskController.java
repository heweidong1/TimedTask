package com.example.timdetask.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.timdetask.domain.*;
import com.example.timdetask.exception.TaskException;
import com.example.timdetask.mapper.SysJobMapper;
import com.example.timdetask.mapper.SysTaskSettingMapper;
import com.example.timdetask.service.ISysJobService;
import com.example.timdetask.utils.AjaxResult;
import com.example.timdetask.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * timed_task_demo
 */
@RestController
@Slf4j
@RequestMapping("/gather")
public class GatherTimeTaskController {



    @Autowired
    private ISysJobService jobService;

    @Autowired
    private SysJobMapper sysJobMapper;


    @Autowired
    private SysTaskSettingMapper sysTaskSettingMapper;


    /**
     * 新建定时发起
     * @return
     */
    @PostMapping("/scheduled")
    @Transactional
    public AjaxResult scheduled(@RequestBody LaunchOnTimeParams launchOnTimeParams) throws SchedulerException, TaskException {
        //判断定时任务时间长度来决定要创建几个定时任务
        if(launchOnTimeParams.getTaskStatus()==0 && !CollectionUtils.isEmpty(launchOnTimeParams.getTaskTimes())){
            //判断是否从草稿箱发起
//            if(launchOnTimeParams.getDraftId()!=null){
//                //物理删除草稿箱里对应的草稿
//                //gfi表中和gei中的记录
//                formInstanceService.deleteById(launchOnTimeParams.getDraftId());
//                gicTimeInstanceMapper.deleteByGfiId(launchOnTimeParams.getDraftId());
//                //将参数DraftId()置为null
//                launchOnTimeParams.setDraftId(null);
//            }
            //生成定时任务组id
             String taskGroupId = String.valueOf(System.currentTimeMillis());
            //创建相应的定时任务，并且创建响应的定时任务组数据
            for (TaskTime taskTime : launchOnTimeParams.getTaskTimes()) {
                //解析为cron表达式
               String cron = parsingToCron(taskTime);
               //新增定时任务
                SysJob sysJob = organizationalParameters(launchOnTimeParams);
                sysJob.setTaskCorn(cron);
                sysJob.setTaskGroupId(taskGroupId);
                //在sys_task表中插入记录，同时创建定时任务
                jobService.insertJob(sysJob);
                //修改定时任务执行方法名称
                sysJob.setTaskClassName("startProcessTask.execute("+ sysJob.getId()+")");
                jobService.updateJob(sysJob);
                //开启
                sysJob.setTaskFlag("0");
                jobService.changeStatus(sysJob);
                //将busisettingId关联到SysTaskSetting表中
                SysTaskSetting sysTaskSetting = new SysTaskSetting();
                //taskID 定时器id
                sysTaskSetting.setTaskId(sysJob.getId().intValue());
                //busiSettingID 记录业务id
                sysTaskSetting.setBusiSettingId(1L);
                sysTaskSettingMapper.insert(sysTaskSetting);
            }
        }
        return AjaxResult.success("新增定时任务成功");
    }

    /**
     * 组织定时任务参数
     * @param launchOnTimeParams
     * @return
     */
    private SysJob organizationalParameters(LaunchOnTimeParams launchOnTimeParams) {
        SysJob sysJob  = new SysJob();
         //定时任务名称 sys_task定时器的名称
        sysJob.setTaskName("业务名称");
         //默认开启
         //描述
        sysJob.setTaskDescription("业务名称定时任务");
        //所属系统
        sysJob.setTaskSystemId(1);
        //开启时间
        sysJob.setStartTime(new Date());
        sysJob.setTaskSystemName("智慧云表");
//        LoginUser loginUser = SecurityUtils.getLoginUser();
//        launchOnTimeParams.setLoginUser(loginUser);
        String params = JSON.toJSONString(launchOnTimeParams);
        sysJob.setRemake1(params);
        return sysJob;
    }


    /**
     * 将对象解析为定时任务表达式
     * @param taskTime
     * @return
     */
    private String parsingToCron(TaskTime taskTime) {
        //分
        Integer minute = taskTime.getMinute();
        //时
        Integer hour =  taskTime.getTime();

        StringBuffer corn = new StringBuffer("0 ");//用此来代表秒

        corn.append(String.valueOf(minute)+" ");//代表分

        corn.append(String.valueOf(hour)+" ");//代表时

        //为每天
        if(taskTime.getIsEveryDay().booleanValue()){
            corn.append("* * ?");
        }
        //每周
        if(taskTime.getIsWeek().booleanValue()){
            //每周周几
             String weekNumber = taskTime.getWeekNumber().toString();
             corn.append("? * "+weekNumber);

        }
        //每月
        if(taskTime.getIsMonth().booleanValue()){
            //每月第几天
            String day = taskTime.getDay().toString();
            corn.append(day+" * ?");
        }
        return corn.toString();
    }

    /**
     *定时任务详情
     * @return
     */
    @GetMapping("/getTaskInfoByTaskGroupId")
    public AjaxResult getTaskInfoByTaskGroupId(@RequestParam(value = "taskGroupId") String taskGroupId){
        //查询出定时任务组下的所有定时任务
        List<SysJob> sysTaskList = sysJobMapper.queryByTaskGroupId(taskGroupId);
        if(CollectionUtils.isEmpty(sysTaskList)){
            return AjaxResult.error("暂无定时任务");
        }
        SysJob sysTask = sysTaskList.get(0);
        String remake1 = sysTask.getRemake1();
        //将参数转换为LaunchOnTimeParams对象
        LaunchOnTimeParams launchOnTimeParams = JSONObject.parseObject(remake1, LaunchOnTimeParams.class);
        launchOnTimeParams.setTaskStatus(Integer.valueOf(sysTask.getTaskFlag()));
        launchOnTimeParams.setNickName("名称");
        return AjaxResult.success(launchOnTimeParams);
    }




    /**
     * 修改定时任务状态
     * @param taskGroupId
     * @param taskStatus
     * @return
     */
    @GetMapping("/updateTaskStatus")
    public AjaxResult updateTaskStatus(@RequestParam(value = "taskGroupId") String taskGroupId,
                                       @RequestParam(value = "taskStatus") Integer taskStatus ){
        String username = "username";
        //查询出定时任务组下的所哟定时任务
        List<SysJob> sysTaskList = sysJobMapper.queryByTaskGroupId(taskGroupId);
        //查询业务是否还存在
//        if(taskStatus==0){
//            LaunchOnTimeParams launchOnTimeParams = JSONObject.parseObject(sysTaskList.get(0).getRemake1(), LaunchOnTimeParams.class);
//            GicBusiSetting gicBusiSetting = busiSettingMapper.selectBusiSettingById(launchOnTimeParams.getGicBusiSetting().getBusiId());
//            if(gicBusiSetting.getStatus()==0){
//                return AjaxResult.error("该业务已关闭，请手动开启该业务");
//            }
//        }
        if (!CollectionUtils.isEmpty(sysTaskList)) {
            sysTaskList.forEach(sysTask -> {
                if (taskStatus == 0) {//关闭->开启
                    sysTask.setStartTime(new Date());
                    sysTask.setTaskFlag(String.valueOf(taskStatus));
                    sysTask.setEndTime(null);
                } else if (taskStatus == 1) {//开启->关闭
                    sysTask.setEndTime(new Date());
                    sysTask.setTaskFlag(String.valueOf(taskStatus));
                }
                sysTask.setUpdateBy(username);
                sysTask.setUpdateTime(new Date());
                try {
                    jobService.changeStatus(sysTask);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            });
        }
        return AjaxResult.success("修改成功");
    }


    /**
     * 获取定时任务列表
     * @return
     */
    @PostMapping("/getTaskList")
    public AjaxResult getTaskList(@RequestBody GetTaskListParams getTaskListParams){
        //        String username = SecurityUtils.getLoginUser().getUsername();
        String username = "create_by";
        List<TaskListResultVo> taskListResultVos = new ArrayList<>();
        //根据人查询
        List<SysJob> sysTaskList = sysJobMapper.queryTaskList(username);
        if (!CollectionUtils.isEmpty(sysTaskList)) {
            for (SysJob sysTask : sysTaskList) {
                LaunchOnTimeParams launchOnTimeParams = JSONObject.parseObject(sysTask.getRemake1(), LaunchOnTimeParams.class);
                TaskListResultVo taskListResultVo = new TaskListResultVo();
                taskListResultVo.setTaskStatus(Integer.valueOf(sysTask.getTaskFlag()));//定时任务状态
                taskListResultVo.setTaskGroupId(sysTask.getTaskGroupId());//定时任务组id
//                taskListResultVo.setBusiId(launchOnTimeParams.getGicBusiSetting().getBusiId());//busiId
//                taskListResultVo.setBusiName(launchOnTimeParams.getGicBusiSetting().getBusiName());//业务名称
                taskListResultVo.setTitle(sysTask.getTaskName());//标题
//                taskListResultVo.setBusiTypeName(gatherMetaData.get(launchOnTimeParams.getGicBusiSetting().getBusiType()));
//                taskListResultVo.setOfficeTypeName(gatherMetaData.get(launchOnTimeParams.getGicBusiSetting().getOfficeType()));
//                taskListResultVo.setOfficeType(launchOnTimeParams.getGicBusiSetting().getOfficeType());
//                taskListResultVo.setBusiType(launchOnTimeParams.getGicBusiSetting().getBusiType());
                taskListResultVo.setStartTime(sysTask.getStartTime());
                taskListResultVo.setInitialTime(sysTask.getStartTime());
                taskListResultVo.setEndTime(sysTask.getEndTime());
                taskListResultVo.setFinishTime(sysTask.getEndTime());
                //taskListResultVo.setCreateBy(iSysUserService.selectByUsername(sysTask.getCreateBy()).getNickName());
                taskListResultVos.add(taskListResultVo);
            }
        }
        //按照条件对业务类型筛选
        if(!CollectionUtils.isEmpty(getTaskListParams.getBusiTypes())){
            taskListResultVos = taskListResultVos.stream().filter(taskListResultVo -> getTaskListParams.getBusiTypes().contains(taskListResultVo.getBusiType())).collect(Collectors.toList());
        }
        //按条件对业务状态筛选
        if(getTaskListParams.getTaskStatus()!=null){
            taskListResultVos = taskListResultVos.stream().filter(taskListResultVo -> taskListResultVo.getTaskStatus() == getTaskListParams.getTaskStatus()).collect(Collectors.toList());
        }
        //按条件对业务名称筛选
        if(StringUtils.isNotEmpty(getTaskListParams.getFind())){
            taskListResultVos = taskListResultVos.stream().filter(taskListResultVo->taskListResultVo.getBusiName().indexOf(getTaskListParams.getFind())>-1).collect(Collectors.toList());
        }
        //按照办公类别筛选
        if(!CollectionUtils.isEmpty(getTaskListParams.getOfficeTypes())){
            taskListResultVos = taskListResultVos.stream().filter(taskListResultVo -> getTaskListParams.getOfficeTypes().contains(taskListResultVo.getOfficeType())).collect(Collectors.toList());
        }

        //按照start_time 排序
        Collections.sort(taskListResultVos, new Comparator<TaskListResultVo>() {
            @Override
            public int compare(TaskListResultVo o1, TaskListResultVo o2) {
                return o2.getStartTime().compareTo(o1.getStartTime());
            }
        });
        //按照状态排序
        Collections.sort(taskListResultVos, new Comparator<TaskListResultVo>() {
            @Override
            public int compare(TaskListResultVo o1, TaskListResultVo o2) {
                return o1.getTaskStatus().compareTo(o2.getTaskStatus());
            }
        });
        return AjaxResult.success(taskListResultVos);
    }

}
