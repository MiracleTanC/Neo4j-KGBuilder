package com.warmer.meta.service.impl;

import com.warmer.base.util.DbUtils;
import com.warmer.meta.entity.MetaDataColumn;
import com.warmer.meta.dao.MetaDataColumnDao;
import com.warmer.meta.entity.MetaDataSource;
import com.warmer.meta.entity.MetaDataTable;
import com.warmer.meta.service.MetaDataColumnService;
import com.warmer.meta.vo.DataColumnVo;
import com.warmer.meta.vo.DataTableVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (MetaDataColumn)表服务实现类
 *
 * @author tanc
 * @since 2021-11-21 11:00:15
 */
@Service("metaDataColumnService")
public class MetaDataColumnServiceImpl implements MetaDataColumnService {
    @Resource
    private MetaDataColumnDao metaDataColumnDao;

    /**
     * 通过ID查询单条数据
     *
     * @param dataColumnId 主键
     * @return 实例对象
     */
    @Override
    public MetaDataColumn queryById(Integer dataColumnId) {
        return this.metaDataColumnDao.queryById(dataColumnId);
    }

    /**
     * 查询多条数据
     *
     * @return 对象列表
     */
    @Override
    public List<MetaDataColumn> queryAll() {
        return this.metaDataColumnDao.queryAll();
    }
    @Override
    public  List<DataColumnVo> queryByTableId(Integer tableId){
        List<MetaDataColumn> dataColumns= this.metaDataColumnDao.queryByTableId(tableId);
        List<DataColumnVo> voList = dataColumns.stream().map(n -> {
            DataColumnVo item = new DataColumnVo();
            item.setDataColumnId(n.getDataColumnId());
            item.setDataColumnName(n.getDataColumnCode());
            item.setDataColumnAlia(n.getDataColumnName());
            item.setDataColumnType(n.getDataColumnType());
            item.setIsPrimary(n.getIsPrimary());
            return item;
        }).collect(Collectors.toList());
        return voList;
    }
    /**
     * 新增数据
     *
     * @param metaDataColumn 实例对象
     * @return 实例对象
     */
    @Override
    public MetaDataColumn insert(MetaDataColumn metaDataColumn) {
        this.metaDataColumnDao.insert(metaDataColumn);
        return metaDataColumn;
    }

    /**
     * 修改数据
     *
     * @param metaDataColumn 实例对象
     * @return 实例对象
     */
    @Override
    public MetaDataColumn update(MetaDataColumn metaDataColumn) {
        this.metaDataColumnDao.update(metaDataColumn);
        return this.queryById(metaDataColumn.getDataColumnId());
    }

    /**
     * 通过主键删除数据
     *
     * @param dataColumnId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer dataColumnId) {
        return this.metaDataColumnDao.deleteById(dataColumnId) > 0;
    }
}