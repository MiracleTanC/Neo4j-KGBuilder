package com.warmer.meta.service;

import com.warmer.meta.entity.MetaDataColumn;
import com.warmer.meta.vo.DataColumnVo;
import com.warmer.meta.vo.DataTableVo;

import java.util.List;

/**
 * (MetaDataColumn)表服务接口
 *
 * @author tanc
 * @since 2021-11-21 11:00:15
 */
public interface MetaDataColumnService {

    /**
     * 通过ID查询单条数据
     *
     * @param dataColumnId 主键
     * @return 实例对象
     */
    MetaDataColumn queryById(Integer dataColumnId);

    /**
     * 查询多条数据
     *
     * @return 对象列表
     */
    List<MetaDataColumn> queryAll();

    List<DataColumnVo> queryByTableId(Integer tableId);
    /**
     * 新增数据
     *
     * @param metaDataColumn 实例对象
     * @return 实例对象
     */
    MetaDataColumn insert(MetaDataColumn metaDataColumn);

    /**
     * 修改数据
     *
     * @param metaDataColumn 实例对象
     * @return 实例对象
     */
    MetaDataColumn update(MetaDataColumn metaDataColumn);

    /**
     * 通过主键删除数据
     *
     * @param dataColumnId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer dataColumnId);

}