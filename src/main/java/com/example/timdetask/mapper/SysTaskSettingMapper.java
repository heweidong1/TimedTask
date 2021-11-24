package com.example.timdetask.mapper;


import com.example.timdetask.domain.SysTaskSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时任务与gic_busi_setting关联表(SysTaskSetting)表数据库访问层
 *
 * @author makejava
 * @since 2021-08-24 22:46:05
 */
public interface SysTaskSettingMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    SysTaskSetting queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<SysTaskSetting> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param sysTaskSetting 实例对象
     * @return 对象列表
     */
    List<SysTaskSetting> queryAll(SysTaskSetting sysTaskSetting);

    /**
     * 新增数据
     *
     * @param sysTaskSetting 实例对象
     * @return 影响行数
     */
    int insert(SysTaskSetting sysTaskSetting);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysTaskSetting> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<SysTaskSetting> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<SysTaskSetting> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<SysTaskSetting> entities);

    /**
     * 修改数据
     *
     * @param sysTaskSetting 实例对象
     * @return 影响行数
     */
    int update(SysTaskSetting sysTaskSetting);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 根据busiid查询关联关系
     * @param busiId
     * @return
     */
    List<SysTaskSetting> selectByBusiSettingId(Long busiId);
}

