package com.warmer.web.service;

import com.warmer.base.util.GraphPageRecord;
import com.warmer.web.model.NodeItem;
import com.warmer.web.request.GraphQuery;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface KgGraphService {
	/**
	 * 领域标签分页
	 * @param queryItem
	 * @return
	 */
	GraphPageRecord<HashMap<String, Object>> getPageDomain(GraphQuery queryItem);
	/**
	 * 删除Neo4j 标签
	 * 
	 * @param domain
	 */
	void deleteKGDomain(String domain);

	/**
	 * 查询图谱节点和关系
	 * 
	 * @param query
	 * @return node relationship
	 */
	HashMap<String, Object> getDomainGraph(GraphQuery query);

	/**
	 * 获取节点列表
	 * 
	 * @param domain
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	HashMap<String, Object> getdomainnodes(String domain, Integer pageIndex, Integer pageSize);

	/**
	 * 获取某个领域指定节点拥有的上下级的节点数
	 * 
	 * @param domain
	 * @param nodeId
	 * @return long 数值
	 */
	long getRelationNodeCount(String domain, long nodeId);

	/**
	 * 创建领域,默认创建一个新的节点,给节点附上默认属性
	 * 
	 * @param domain
	 */
	void createDomain(String domain);
	void quickCreateDomain(String domain,String nodeName);

	/**
	 * 获取/展开更多节点,找到和该节点有关系的节点
	 * 
	 * @param domain
	 * @param nodeId
	 * @return
	 */
	HashMap<String, Object> getMoreRelationNode(String domain, String nodeId);

	/**
	 * 更新节点名称
	 * 
	 * @param domain
	 * @param nodeId
	 * @param nodeName
	 * @return 修改后的节点
	 */
	HashMap<String, Object> updateNodeName(String domain, String nodeId, String nodeName);

	/**
	 * 创建单个节点
	 * 
	 * @param domain
	 * @param entity
	 * @return
	 */
	HashMap<String, Object> createNode(String domain, NodeItem entity);

	/**
	 * 批量创建节点和关系
	 * 
	 * @param domain
	 *            领域
	 * @param sourceName
	 *            源节点
	 * @param relation
	 *            关系
	 * @param targetNames
	 *            目标节点数组
	 * @return
	 */
	HashMap<String, Object> batchCreateNode(String domain, String sourceName, String relation, String[] targetNames);

	/**
	 * 批量创建下级节点
	 * 
	 * @param domain
	 *            领域
	 * @param sourceId
	 *            源节点id
	 * @param entityType
	 *            节点类型
	 * @param targetNames
	 *            目标节点名称数组
	 * @param relation
	 *            关系
	 * @return
	 */
	HashMap<String, Object> batchCreateChildNode(String domain, String sourceId, Integer entityType,
			String[] targetNames, String relation);

	/**
	 * 批量创建同级节点
	 * 
	 * @param domain
	 *            领域
	 * @param entityType
	 *            节点类型
	 * @param sourceNames
	 *            节点名称
	 * @return
	 */
	List<HashMap<String, Object>> batchCreateSameNode(String domain, Integer entityType, String[] sourceNames);

	/**
	 * 添加关系
	 * 
	 * @param domain
	 *            领域
	 * @param sourceId
	 *            源节点id
	 * @param targetId
	 *            目标节点id
	 * @param ship
	 *            关系
	 * @return
	 */
	HashMap<String, Object> createLink(String domain, long sourceId, long targetId, String ship);

	/**
	 * 更新关系
	 * 
	 * @param domain
	 *            领域
	 * @param shipId
	 *            关系id
	 * @param shipName
	 *            关系名称
	 * @return
	 */
	HashMap<String, Object> updateLink(String domain, long shipId, String shipName);

	/**
	 * 删除节点(先删除关系再删除节点)
	 * 
	 * @param domain
	 * @param nodeId
	 * @return
	 */
	List<HashMap<String, Object>> deleteNode(String domain, long nodeId);

	/**
	 * 删除关系
	 * 
	 * @param domain
	 * @param shipId
	 */
	void deleteLink(String domain, long shipId);

	/**
	 * 段落识别出的三元组生成图谱
	 * 
	 * @param domain
	 * @param entityType
	 * @param operateType
	 * @param sourceId
	 * @param rss
	 *            关系三元组
	 *            [[startname;ship;endname],[startname1;ship1;endname1],[startname2;ship2;endname2]]
	 * @return node relationship
	 */
	HashMap<String, Object> createGraphByText(String domain, Integer entityType, Integer operateType, Integer sourceId,
			String[] rss);
	/**
	 * 批量创建节点，关系
	 * @param domain
	 * @param params 三元组 sourceNode,relationship,targetNode
	 */
	void batchcreateGraph(String domain, List<Map<String,Object>> params);
	/**
	 * 导入csv
	 * @param domain
	 * @param csvUrl
	 * @param status
	 */
	void batchInsertByCSV(String domain, String csvUrl, int status) ;
	/**
	 * 更新节点有无附件
	 * @param domain
	 * @param nodeId
	 * @param status
	 */
	void updateNodeFileStatus(String domain,long nodeId, int status);
	void updateCoordinateOfNode(String domain, String uuid, Double fx, Double fy);

	void importBySyz(MultipartFile file, HttpServletRequest request,String label,Integer isCreateIndex)throws Exception ;
	void importByCategory(MultipartFile file, HttpServletRequest request,String label)throws Exception ;
}
