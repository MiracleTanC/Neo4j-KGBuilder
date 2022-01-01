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
    KgGraphNodeMap queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<KgGraphNodeMap> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param kgGraphNodeMap 实例对象
     * @return 实例对象
     */
    KgGraphNodeMap insert(KgGraphNodeMap kgGraphNodeMap);

    /**
     * 修改数据
     *
     * @param kgGraphNodeMap 实例对象
     * @return 实例对象
     */
    KgGraphNodeMap update(KgGraphNodeMap kgGraphNodeMap);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}