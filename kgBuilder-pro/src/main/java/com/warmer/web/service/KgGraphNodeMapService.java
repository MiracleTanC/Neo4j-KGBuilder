package com.warmer.web.service;

import com.warmer.web.entity.KgGraphNodeMap;
import java.util.List;

/**
 * 图谱节点映射管理服务接口
 *
 * 管理领域内节点到图谱节点的映射关系查询与新增。
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
