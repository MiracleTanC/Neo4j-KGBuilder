package com.warmer.web.service;

import com.warmer.base.util.GraphPageRecord;
import com.warmer.web.model.NodeItem;
import com.warmer.web.request.GraphQuery;
import com.warmer.web.request.NodeCoordinateItem;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图谱构建与可视化服务接口
 *
 * 负责领域的图谱数据增删改查、节点与关系的批量/单体操作、坐标更新、
 * 附件与图片管理，以及多种导入方式（CSV/Excel/文本三元组）。
 */
public interface KGGraphService {
	/**
	 * 领域标签分页查询
	 * @param queryItem 查询条件对象
	 * @return 分页记录
	 */
	GraphPageRecord<HashMap<String, Object>> getPageDomain(GraphQuery queryItem);

	/**
	 * 删除图谱中的领域（标签）
	 * @param domain 领域名称
	 */
	void deleteKGDomain(String domain);

	/**
	 * 查询图谱节点和关系（用于可视化展示）
	 * @param query 查询条件
	 * @return 包含节点和关系的Map
	 */
	HashMap<String, Object> queryGraphResult(GraphQuery query);

	/**
	 * 获取指定领域的节点列表
	 * @param domain 领域名称
	 * @param pageIndex 当前页码
	 * @param pageSize 每页数量
	 * @return 节点列表Map
	 */
	HashMap<String, Object> getdomainnodes(String domain, Integer pageIndex, Integer pageSize);

	/**
	 * 获取某个领域指定节点拥有的上下级节点数量
	 * @param domain 领域名称
	 * @param nodeId 节点ID
	 * @return 关联节点总数
	 */
	long getRelationNodeCount(String domain, long nodeId);

	/**
	 * 创建领域（仅创建标签索引）
	 * @param domain 领域名称
	 */
	void createDomain(String domain);

	/**
	 * 快速创建领域（创建标签并初始化一个默认节点）
	 * @param domain 领域名称
	 * @param nodeName 默认节点名称
	 */
	void quickCreateDomain(String domain,String nodeName);

	/**
	 * 获取更多关联节点（展开节点）
	 * @param domain 领域名称
	 * @param nodeId 节点ID
	 * @return 关联的节点和关系
	 */
	HashMap<String, Object> getMoreRelationNode(String domain, String nodeId);

	/**
	 * 更新节点名称
	 * @param domain 领域名称
	 * @param nodeId 节点ID
	 * @param nodeName 新节点名称
	 * @return 更新后的节点信息
	 */
	HashMap<String, Object> updateNodeName(String domain, String nodeId, String nodeName);

	/**
	 * 创建单个节点
	 * @param domain 领域名称
	 * @param entity 节点信息实体
	 * @return 创建后的节点信息
	 */
	HashMap<String, Object> createNode(String domain, NodeItem entity);

	/**
	 * 批量创建节点和关系（以源节点为中心）
	 * @param domain 领域名称
	 * @param sourceName 源节点名称
	 * @param relation 关系名称
	 * @param targetNames 目标节点名称数组
	 * @return 创建结果
	 */
	HashMap<String, Object> batchCreateNode(String domain, String sourceName, String relation, String[] targetNames);

	/**
	 * 批量创建下级节点
	 * @param domain 领域名称
	 * @param sourceId 源节点ID
	 * @param entityType 节点类型
	 * @param targetNames 目标节点名称数组
	 * @param relation 关系名称
	 * @return 创建结果
	 */
	HashMap<String, Object> batchCreateChildNode(String domain, String sourceId, Integer entityType,
			String[] targetNames, String relation);

	/**
	 * 批量创建同级节点
	 * @param domain 领域名称
	 * @param entityType 节点类型
	 * @param sourceNames 节点名称数组
	 * @return 创建成功的节点列表
	 */
	List<HashMap<String, Object>> batchCreateSameNode(String domain, Integer entityType, String[] sourceNames);

	/**
	 * 创建关系（连线）
	 * @param domain 领域名称
	 * @param sourceId 源节点ID
	 * @param targetId 目标节点ID
	 * @param ship 关系名称
	 * @return 创建的关系信息
	 */
	HashMap<String, Object> createLink(String domain, long sourceId, long targetId, String ship);

	/**
	 * 更新关系名称
	 * @param domain 领域名称
	 * @param shipId 关系ID
	 * @param shipName 新关系名称
	 * @return 更新后的关系信息
	 */
	HashMap<String, Object> updateLink(String domain, long shipId, String shipName);

	/**
	 * 删除节点（级联删除关系）
	 * @param domain 领域名称
	 * @param nodeId 节点ID
	 * @return 删除结果
	 */
	List<HashMap<String, Object>> deleteNode(String domain, long nodeId);

	/**
	 * 删除关系
	 * @param domain 领域名称
	 * @param shipId 关系ID
	 */
	void deleteLink(String domain, long shipId);

	/**
	 * 根据文本三元组生成图谱
	 * @param domain 领域名称
	 * @param entityType 实体类型
	 * @param operateType 操作类型
	 * @param sourceId 源节点ID
	 * @param rss 三元组数组 [[start;ship;end], ...]
	 * @return 图谱数据
	 */
	HashMap<String, Object> createGraphByText(String domain, Integer entityType, Integer operateType, Integer sourceId,
			String[] rss);

	/**
	 * 批量导入图谱数据
	 * @param domain 领域名称
	 * @param params 数据列表（包含sourceNode, relationship, targetNode）
	 */
	void batchCreateGraph(String domain, List<Map<String,Object>> params);

	/**
	 * 从CSV文件批量导入
	 * @param domain 领域名称
	 * @param csvUrl CSV文件路径
	 * @param status 状态标识
	 */
	void batchInsertByCSV(String domain, String csvUrl, int status) ;

	/**
	 * 更新节点附件状态
	 * @param domain 领域名称
	 * @param nodeId 节点ID
	 * @param status 状态（0:无, 1:有）
	 */
	void updateNodeFileStatus(String domain,long nodeId, int status);

	/**
	 * 更新节点图片路径
	 * @param domain 领域名称
	 * @param nodeId 节点ID
	 * @param img 图片路径
	 */
	void updateNodeImg(String domain, long nodeId, String img);

	/**
	 * 移除节点图片
	 * @param domain 领域名称
	 * @param nodeId 节点ID
	 */
	void removeNodeImg(String domain, long nodeId);

	/**
	 * 更新单个节点坐标
	 * @param domain 领域名称
	 * @param uuid 节点UUID/ID
	 * @param fx X坐标
	 * @param fy Y坐标
	 */
	void updateCoordinateOfNode(String domain, String uuid, Double fx, Double fy);

	/**
	 * 批量更新节点坐标
	 * @param domain 领域名称
	 * @param nodes 节点坐标列表
	 */
	void batchUpdateGraphNodesCoordinate(String domain,List<NodeCoordinateItem> nodes);

	/**
	 * 导入三元组Excel数据
	 * @param file Excel文件
	 * @param request 请求对象
	 * @param label 领域标签
	 * @param isCreateIndex 是否创建索引
	 * @throws Exception 异常
	 */
	void importBySyz(MultipartFile file, HttpServletRequest request,String label,Integer isCreateIndex)throws Exception ;

	/**
	 * 导入分类Excel数据
	 * @param file Excel文件
	 * @param request 请求对象
	 * @param label 领域标签
	 * @throws Exception 异常
	 */
	void importByCategory(MultipartFile file, HttpServletRequest request,String label)throws Exception ;
}
