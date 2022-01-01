package com.warmer.meta.dao;

import com.warmer.meta.entity.MetaDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 数据源(MetaDataSource)表数据库访问层
 *
 * @author tanc
 * @since 2021-11-21 11:00:18
 */
@Mapper
public interface MetaDataSourceDao {

    /**
     * 通过ID查询单条数据
     *
     * @param dataSourceId 主键
     * @return 实例对象
     */
    MetaDataSource queryById(Integer dataSourceId);

    /**
     * 通过实体作为筛选条件查询
     *
     * @return 对象列表
     */
    List<MetaDataSource> queryAll();

    /**
     * 新增数据
     *
     * @param metaDataSource 实例对象
     * @return 影响行数
     */
    int insert(MetaDataSource metaDataSource);

    /**
     * 修改数据
     *
     * @param metaDataSource 实例对象
     * @return 影响行数
     */
    int update(MetaDataSource metaDataSource);

    /**
     * 通过主键删除数据
     *
     * @param dataSourceId 主键
     * @return 影响行数
     */
    int deleteById(Integer dataSourceId);

}