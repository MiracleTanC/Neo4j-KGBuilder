package com.warmer.meta.dao;

import com.warmer.meta.entity.MetaDataTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (MetaDataTable)表数据库访问层
 *
 * @author tanc
 * @since 2021-11-21 11:00:21
 */
@Mapper
public interface MetaDataTableDao {

    /**
     * 通过ID查询单条数据
     *
     * @param dataTableId 主键
     * @return 实例对象
     */
    MetaDataTable queryById(Integer dataTableId);

    /**
     * 通过实体作为筛选条件查询
     *
     * @return 对象列表
     */
    List<MetaDataTable> queryAll();
    List<MetaDataTable> queryByDatasourceId(@Param("datasourceId") Integer datasourceId);
    MetaDataTable queryByDatasourceIdAndTableCode(@Param("datasourceId") Integer datasourceId,@Param("dataTableCode") String dataTableCode);

    /**
     * 新增数据
     *
     * @param metaDataTable 实例对象
     * @return 影响行数
     */
    int insert(MetaDataTable metaDataTable);

    /**
     * 修改数据
     *
     * @param metaDataTable 实例对象
     * @return 影响行数
     */
    int update(MetaDataTable metaDataTable);

    /**
     * 通过主键删除数据
     *
     * @param dataTableId 主键
     * @return 影响行数
     */
    int deleteById(Integer dataTableId);

}