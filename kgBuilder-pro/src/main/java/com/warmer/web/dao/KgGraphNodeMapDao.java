package com.warmer.web.dao;

import com.warmer.web.entity.KgGraphNodeMap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
/**
 * 图谱节点映射数据访问接口
 *
 * 负责领域内节点与图谱节点之间的映射关系的增删查操作。
 */
public interface KgGraphNodeMapDao {

    /**
     * 插入单条映射记录
     * @param record 映射实体
     * @return 受影响行数
     */
    int insert(KgGraphNodeMap record);

    /**
     * 批量插入映射记录
     * @param records 映射实体列表
     * @return 受影响行数
     */
    int batchInsert(List<KgGraphNodeMap> records);

    /**
     * 根据领域ID删除所有映射记录
     * @param domainId 领域ID
     * @return 受影响行数
     */
    int deleteByDomainId(Integer domainId);

    /**
     * 根据领域ID查询映射记录
     * @param domainId 领域ID
     * @return 映射记录列表
     */
    List<KgGraphNodeMap> selectByDomainId(Integer domainId);

    /**
     * 根据领域ID与节点ID查询映射记录
     * @param domainId 领域ID
     * @param nodeId 节点ID
     * @return 映射记录列表
     */
    List<KgGraphNodeMap> selectByDomainIdAndNodeId(Integer domainId,Long nodeId);
}
