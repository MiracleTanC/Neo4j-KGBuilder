package com.warmer.meta.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.warmer.base.util.DbUtils;
import com.warmer.meta.dto.DataTableSubmitItem;
import com.warmer.meta.entity.MetaDataSource;
import com.warmer.meta.dao.MetaDataSourceDao;
import com.warmer.meta.entity.MetaDataTable;
import com.warmer.meta.service.MetaDataSourceService;
import com.warmer.meta.vo.DatasourceVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据源(MetaDataSource)表服务实现类
 *
 * @author tanc
 * @since 2021-11-21 11:00:19
 */
@Service("metaDataSourceService")
public class MetaDataSourceServiceImpl implements MetaDataSourceService {
    @Resource
    private MetaDataSourceDao metaDataSourceDao;

    /**
     * 通过ID查询单条数据
     *
     * @param dataSourceId 主键
     * @return 实例对象
     */
    @Override
    public MetaDataSource queryById(Integer dataSourceId) {
        return this.metaDataSourceDao.queryById(dataSourceId);
    }

    @Override
    public List<DatasourceVo> queryList() {
        List<MetaDataSource> dataSources= this.metaDataSourceDao.queryAll();
        List<DatasourceVo> voList = dataSources.stream().map(n -> {
            DatasourceVo item = new DatasourceVo();
            item.setDatasourceId(n.getDataSourceId());
            item.setDatasourceAlia(n.getDbName());
            item.setDatasourceName(n.getDbCode());
            item.setDatasourceType(n.getDbType());
            return item;
        }).collect(Collectors.toList());
       return voList;
    }
    /**
     * 查询多条数据
     *
     * @return 对象列表
     */
    @Override
    public List<MetaDataSource> queryAll() {
        return this.metaDataSourceDao.queryAll();
    }
    /**
     * 新增数据
     *
     * @param metaDataSource 实例对象
     * @return 实例对象
     */
    @Override
    public MetaDataSource insert(MetaDataSource metaDataSource) {
        this.metaDataSourceDao.insert(metaDataSource);
        return metaDataSource;
    }

    /**
     * 修改数据
     *
     * @param metaDataSource 实例对象
     * @return 实例对象
     */
    @Override
    public MetaDataSource update(MetaDataSource metaDataSource) {
        this.metaDataSourceDao.update(metaDataSource);
        return this.queryById(metaDataSource.getDataSourceId());
    }

    /**
     * 通过主键删除数据
     *
     * @param dataSourceId 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer dataSourceId) {
        return this.metaDataSourceDao.deleteById(dataSourceId) > 0;
    }

    public HashMap<String, ArrayList<HashMap<String, String>>> getMetaData(MetaDataSource metaDataSource) {
        String dbType = metaDataSource.getDbType();
        String userName = metaDataSource.getDbUserName();
        String password = metaDataSource.getDbPassWord();
        String driverClassName = metaDataSource.getDriverName();
        HashMap<String, ArrayList<HashMap<String, String>>> result = null;
        try {
            result = DbUtils.getMetaData(dbType, metaDataSource.getDbCode(), metaDataSource.getConnectUrl(), userName, password,
                    driverClassName);
        } catch (Exception e) {
            throw e;
        }
        return result;
    }


}