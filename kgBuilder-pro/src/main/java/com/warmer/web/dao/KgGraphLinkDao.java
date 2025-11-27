package com.warmer.web.dao;

import com.warmer.web.entity.KgGraphLink;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
/**
 * 图谱关系数据访问接口
 *
 * 提供关系按领域查询、插入、批量插入与按领域删除方法。
 */
public interface KgGraphLinkDao {
    /**
     * 根据领域ID查询关系列表
     * @param domainId 领域ID
     * @return 关系列表
     */
    List<KgGraphLink> selectByDomainId(Integer domainId);

    /**
     * 插入单条关系记录
     * @param record 关系实体
     * @return 受影响行数
     */
    int insert(KgGraphLink record);

    /**
     * 批量插入关系记录
     * @param records 关系实体列表
     * @return 受影响行数
     */
    int batchInsert(List<KgGraphLink> records);

    /**
     * 根据领域ID删除所有关系
     * @param domainId 领域ID
     * @return 受影响行数
     */
    int deleteByDomainId(Integer domainId);
}
