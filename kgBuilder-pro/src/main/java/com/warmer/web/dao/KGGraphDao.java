package com.warmer.web.dao;


import com.warmer.web.model.NodeItem;
import com.warmer.web.request.GraphQuery;
import com.warmer.base.util.GraphPageRecord;
import com.warmer.web.request.NodeCoordinateItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Mapper
public interface KGGraphDao {
    /**
     * 领域标签分页
     * @param queryItem 查询条件（页码、大小、匹配条件等）
     * @return 分页记录，含 `nodeList/totalCount/pageIndex/pageSize`
     */
    GraphPageRecord<HashMap<String, Object>> getPageDomain(GraphQuery queryItem);

    /**
     * 删除领域标签对应的所有节点与关系
     * @param domain 领域标签名称
     */
    void deleteKgDomain(String domain);

    /**
     * 查询图谱节点与关系（用于可视化展示）
     * @param query 查询条件（领域、关系过滤、节点关键词等）
     * @return Map，包含 `node` 与 `relationship`
     */
    HashMap<String, Object> queryGraphResult(GraphQuery query);

    /**
     * 分页获取领域内节点列表并按类型聚合
     * @param domain 领域标签
     * @param pageIndex 当前页码
     * @param pageSize 每页数量
     * @return Map，包含概念/属性/方法/实体集合
     */
    HashMap<String, Object> getDomainNodes(String domain, Integer pageIndex, Integer pageSize);

    /**
     * 获取指定节点的上下级关联节点数量
     * @param domain 领域标签
     * @param nodeId 节点ID
     * @return 关联节点数量
     */
    long getRelationNodeCount(String domain, String nodeId);

    /**
     * 创建领域（默认创建一个空节点并赋默认属性）
     * @param domain 领域标签
     */
    void createDomain(String domain);

    /**
     * 快速创建领域（创建一个指定名称的默认节点）
     * @param domain 领域标签
     * @param nodeName 默认节点名称
     */
    void quickCreateDomain(String domain,String nodeName);

    /**
     * 展开更多与指定节点有关的节点与关系
     * @param domain 领域标签
     * @param nodeId 节点ID
     * @return Map，包含节点与关系
     */
    HashMap<String, Object> getMoreRelationNode(String domain, String nodeId);

    /**
     * 更新节点名称
     * @param domain 领域标签
     * @param nodeId 节点ID
     * @param nodeName 新名称
     * @return 更新后的节点信息
     */
    HashMap<String, Object> updateNodeName(String domain, String nodeId, String nodeName);

    /**
     * 创建单个节点
     * @param domain 领域标签
     * @param entity 节点实体
     * @return 创建或更新后的节点信息
     */
    HashMap<String, Object> createNode(String domain, NodeItem entity);

    /**
     * 按自定义 uuid 创建节点（存在则更新返回，不存在则创建）
     * @param domain 领域标签
     * @param entity 节点实体
     * @return 节点信息
     */
    HashMap<String, Object> createNodeWithUUid(String domain, NodeItem entity);

    /**
     * 批量创建节点和关系（以源节点为中心）
     * @param domain 领域标签
     * @param sourceName 源节点名称
     * @param relation 关系名称
     * @param targetNames 目标节点名称数组
     * @return Map，包含新建的节点与关系集合
     */
    HashMap<String, Object> batchCreateNode(String domain, String sourceName, String relation, String[] targetNames);

    /**
     * 批量创建下级节点
     * @param domain 领域标签
     * @param sourceId 源节点ID
     * @param entityType 节点类型
     * @param targetNames 目标节点名称数组
     * @param relation 关系名称
     * @return Map，包含新建的节点与关系集合
     */
    HashMap<String, Object> batchCreateChildNode(String domain, String sourceId, Integer entityType,
                                                 String[] targetNames, String relation);

    /**
     * 批量创建同级节点
     * @param domain 领域标签
     * @param entityType 节点类型
     * @param sourceNames 节点名称数组
     * @return 节点集合
     */
    List<HashMap<String, Object>> batchCreateSameNode(String domain, Integer entityType, String[] sourceNames);

    /**
     * 创建关系
     * @param domain 领域标签
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @param ship 关系名称
     * @return 关系信息
     */
    HashMap<String, Object> createLink(String domain, String sourceId, String targetId, String ship);

    /**
     * 创建关系（按 uuid）
     * @param domain 领域标签
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @param ship 关系名称
     * @return 关系信息
     */
    HashMap<String, Object> createLinkByUuid(String domain, String sourceId, String targetId, String ship);

    /**
     * 更新关系名称
     * @param domain 领域标签
     * @param shipId 关系ID
     * @param shipName 新关系名称
     * @return 更新后的关系信息
     */
    HashMap<String, Object> updateLink(String domain, String shipId, String shipName);

    /**
     * 删除节点（先删除其关系，再删除节点本身）
     * @param domain 领域标签
     * @param nodeId 节点ID
     * @return 删除过程中涉及的节点列表
     */
    List<HashMap<String, Object>> deleteNode(String domain, String nodeId);

    /**
     * 删除关系
     * @param domain 领域标签
     * @param shipId 关系ID
     */
    void deleteLink(String domain, String shipId);

    /**
     * 文本三元组生成图谱
     * @param domain 领域标签
     * @param entityType 实体类型
     * @param operateType 操作类型
     * @param sourceId 源节点ID
     * @param rss 关系三元组数组 [[start;ship;end], ...]
     * @return Map，包含节点与关系
     */
    HashMap<String, Object> createGraphByText(String domain, Integer entityType, Integer operateType, Integer sourceId,
            String[] rss);

    /**
     * 批量创建节点与关系（三元组）
     * @param domain 领域标签
     * @param params 三元组 `sourceNode, relationship, targetNode`
     */
    void batchCreateGraph(String domain, List<Map<String,Object>> params);

    /**
     * 批量更新节点坐标
     * @param domain 领域标签
     * @param params 节点坐标列表
     */
    void batchUpdateGraphNodesCoordinate(String domain,List<NodeCoordinateItem> params);

    /**
     * 更新节点附件状态
     * @param domain 领域标签
     * @param nodeId 节点ID
     * @param status 状态（0:无,1:有）
     */
    void updateNodeFileStatus(String domain,String nodeId, int status);

    /**
     * 更新节点图片路径
     * @param domain 领域标签
     * @param nodeId 节点ID
     * @param img 图片路径
     */
    void updateNodeImg(String domain, String nodeId, String img);

    /**
     * 移除节点图片
     * @param domain 领域标签
     * @param nodeId 节点ID
     */
    void removeNodeImg(String domain, String nodeId);

    /**
     * 导入 CSV 三元组数据
     * @param domain 领域标签
     * @param csvUrl CSV 文件路径
     * @param status 状态标识
     */
    void batchInsertByCsv(String domain, String csvUrl, int status) ;

    /**
     * 更新单个节点坐标
     * @param domain 领域标签
     * @param uuid 节点UUID/ID
     * @param fx X坐标
     * @param fy Y坐标
     */
    void updateCoordinateOfNode(String domain, String uuid, Double fx, Double fy);
}
