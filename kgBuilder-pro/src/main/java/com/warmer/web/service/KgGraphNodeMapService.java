package com.warmer.web.service;

import com.warmer.web.entity.KgGraphNodeMap;
import java.util.List;

/**
 * (KgGraphNodeMap)表服务接口
 *
 * @author tanc
 * @since 2021-12-24 15:53:51
 */
public interface KgGraphNodeMapService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    List<KgGraphNodeMap> queryById(Integer id);


    /**
     * 新增数据
     *
     * @param kgGraphNodeMap 实例对象
     * @return 实例对象
     */
    Integer insert(KgGraphNodeMap kgGraphNodeMap);


}