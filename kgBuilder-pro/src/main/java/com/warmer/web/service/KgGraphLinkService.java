package com.warmer.web.service;

import com.warmer.web.entity.KgGraphLink;
import java.util.List;

/**
 * 图谱关系管理服务接口
 *
 * 提供按领域查询关系与新增关系的能力。
 */
public interface KgGraphLinkService {

    /**
     * 通过ID查询单条数据
     *
     * @param domainId 领域id
     * @return 实例对象
     */
    List<KgGraphLink> queryById(Integer domainId);


    /**
     * 新增数据
     *
     * @param kgGraphLink 实例对象
     * @return 实例对象
     */
    Integer insert(KgGraphLink kgGraphLink);


}
