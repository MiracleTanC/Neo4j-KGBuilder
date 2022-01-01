package com.warmer.meta.dao;

import com.warmer.meta.entity.MetaDataColumn;
import com.warmer.meta.vo.DataTableVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (MetaDataColumn)表数据库访问层
 *
 * @author tanc
 * @since 2021-11-21 11:00:14
 */
@Mapper
public interface MetaDataColumnDao {

    /**
     * 通过ID查询单条数据
     *
     * @param dataColumnId 主键
     * @return 实例对象
     */
    MetaDataColumn queryById(Integer dataColumnId);

    /**
     * 通过实体作为筛选条件查询
     *
     * @return 对象列表
     */
    List<MetaDataColumn> queryAll();
    List<MetaDataColumn> queryByTableId(@Param("dataTableId") Integer dataTableId);
    /**
     * 新增数据
     *
     * @param metaDataColumn 实例对象
     * @return 影响行数
     */
    int insert(MetaDataColumn metaDataColumn);

    /**
     * 修改数据
     *
     * @param metaDataColumn 实例对象
     * @return 影响行数
     */
    int update(MetaDataColumn metaDataColumn);

    /**
     * 通过主键删除数据
     *
     * @param dataColumnId 主键
     * @return 影响行数
     */
    int deleteById(Integer dataColumnId);

}