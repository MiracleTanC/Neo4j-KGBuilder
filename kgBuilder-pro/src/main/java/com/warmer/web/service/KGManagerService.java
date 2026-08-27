package com.warmer.web.service;


import com.warmer.web.entity.KgDomain;
import com.warmer.web.entity.KgNodeDetail;
import com.warmer.web.entity.KgNodeDetailFile;

import java.util.List;
import java.util.Map;

/**
 * 领域与节点内容管理服务接口
 *
 * 负责领域基本信息的维护（查询/新增/更新/删除），以及节点富文本与图片内容的存取。
 */
public interface KGManagerService {
	/**
	 * 获取所有领域
	 * @return 领域列表
	 */
	List<KgDomain> getDomains();

	/**
	 * 分页查询领域列表
	 * @param domainName 领域名称(模糊查询)
	 * @param type 领域类型
	 * @param commend 是否推荐
	 * @return 领域列表
	 */
	List<KgDomain> getDomainList(String domainName,Integer type,Integer commend);

	/**
	 * 保存领域信息
	 * @param map 领域实体
	 * @return 保存后的ID
	 */
	Integer saveDomain(KgDomain map);

	/**
	 * 快速创建领域
	 * @param domain 领域名称
	 * @param domainAlia 领域别名
	 * @param type 领域类型
	 * @return 创建后的ID
	 */
	Integer quickCreateDomain(String domain,String domainAlia,Integer type);

	/**
	 * 更新领域信息
	 * @param map 领域实体
	 */
	void updateDomain(KgDomain map);

	/**
	 * 删除领域
	 * @param id 领域ID
	 */
	void deleteDomain(Integer id);

	/**
	 * 根据名称获取领域
	 * @param domainName 领域名称
	 * @return 领域列表
	 */
	List<KgDomain> getDomainByName(String domainName);

	/**
	 * 根据标签获取领域
	 * @param label 标签
	 * @return 领域实体
	 */
	KgDomain getDomainByLabel(String label);

	/**
	 * 根据ID获取领域列表
	 * @param domainId 领域ID
	 * @return 领域列表
	 */
	List<KgDomain> getDomainById(Integer domainId);

	/**
	 * 根据ID获取单个领域详情
	 * @param domainId 领域ID
	 * @return 领域实体
	 */
	KgDomain selectById(Integer domainId);

	/**
	 * 保存节点图片信息
	 * @param mapList 图片信息列表
	 */
	void saveNodeImage(List<Map<String, Object>> mapList);

	/**
	 * 保存节点内容
	 * @param map 内容信息
	 */
	void saveNodeContent(Map<String, Object> map);

	/**
	 * 更新节点内容
	 * @param map 内容信息
	 */
	void updateNodeContent(Map<String, Object> map);

	/**
	 * 获取节点图片列表
	 * @param domainId 领域ID
	 * @param nodeId 节点ID
	 * @return 图片列表
	 */
	List<KgNodeDetailFile> getNodeImageList(Integer domainId, String nodeId);

	/**
	 * 获取节点内容详情
	 * @param domainId 领域ID
	 * @param nodeId 节点ID
	 * @return 内容列表
	 */
	List<KgNodeDetail> getNodeContent(Integer domainId, String nodeId);

	/**
	 * 删除节点图片
	 * @param domainId 领域ID
	 * @param nodeId 节点ID
	 */
	void deleteNodeImage(Integer domainId,String nodeId);
}
