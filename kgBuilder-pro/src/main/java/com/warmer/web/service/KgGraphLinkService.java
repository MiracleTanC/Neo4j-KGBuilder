package com.warmer.web.service;

import com.warmer.web.entity.KgGraphLink;
import java.util.List;

/**
 * (KgGraphLink)表服务接口
 *
 * @author tanc
 * @since 2021-12-24 15:53:53
 */
public interface KgGraphLinkService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    KgGraphLink queryById(Long id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<KgGraphLink> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param kgGraphLink 实例对象
     * @return 实例对象
     */
    KgGraphLink insert(KgGraphLink kgGraphLink);

    /**
     * 修改数据
     *
     * @param kgGraphLink 实例对象
     * @return 实例对象
     */
    KgGraphLink update(KgGraphLink kgGraphLink);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}