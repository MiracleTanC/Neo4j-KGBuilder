package com.warmer.web.dao;

import com.warmer.web.entity.KgGraphNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
/**
 * 图谱节点数据访问接口
 *
 * 提供节点按领域查询、插入、批量插入与按领域删除方法。
 */
public interface KgGraphNodeDao {
    /**
     * 根据领域ID查询节点列表
     * @param domainId 领域ID
     * @return 节点列表
     */
    List<KgGraphNode> selectByDomainId(Integer domainId);

    /**
     * 插入单个节点记录
     * @param record 节点实体
     * @return 受影响行数
     */
    int insert(KgGraphNode record);

    /**
     * 批量插入节点记录
     * @param record 节点实体列表
     * @return 受影响行数
     */
    int batchInsert(List<KgGraphNode> record);

    /**
     * 根据领域ID删除所有节点
     * @param domainId 领域ID
     * @return 受影响行数
     */
    int deleteByDomainId(Integer domainId);
}
