package com.warmer.meta.service;

import com.warmer.meta.entity.MetaDataSource;
import com.warmer.meta.vo.DatasourceVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 数据源(MetaDataSource)表服务接口
 *
 * @author tanc
 * @since 2021-11-21 11:00:18
 */
public interface MetaDataSourceService {

    /**
     * 通过ID查询单条数据
     *
     * @param dataSourceId 主键
     * @return 实例对象
     */
    MetaDataSource queryById(Integer dataSourceId);

    /**
     * 查询多条数据
     *
     * @return 对象列表
     */
    List<MetaDataSource> queryAll();

    List<DatasourceVo> queryList();
    /**
     * 新增数据
     *
     * @param metaDataSource 实例对象
     * @return 实例对象
     */
    MetaDataSource insert(MetaDataSource metaDataSource);

    /**
     * 修改数据
     *
     * @param metaDataSource 实例对象
     * @return 实例对象
     */
    MetaDataSource update(MetaDataSource metaDataSource);

    /**
     * 通过主键删除数据
     *
     * @param dataSourceId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer dataSourceId);

    HashMap<String, ArrayList<HashMap<String, String>>> getMetaData(MetaDataSource metaDataSource);


}