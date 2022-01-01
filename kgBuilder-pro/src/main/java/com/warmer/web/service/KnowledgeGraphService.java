package com.warmer.web.service;


import com.warmer.web.entity.KgDomain;
import com.warmer.web.entity.KgNodeDetail;
import com.warmer.web.entity.KgNodeDetailFile;

import java.util.List;
import java.util.Map;

public interface KnowledgeGraphService {
	List<KgDomain> getDomains();
	List<KgDomain> getRecommendDomainList();
	List<KgDomain> getDomainList(String domainName,Integer type,Integer commend);
	Integer saveDomain(KgDomain map);
	Integer quickCreateDomain(String domain,Integer type);
	void updateDomain(KgDomain map);
	void deleteDomain(Integer id);
	List<KgDomain> getDomainByName(String domainName);
	List<KgDomain> getDomainById(Integer domainId);
	KgDomain selectById(Integer domainId);
	void saveNodeImage(List<Map<String, Object>> mapList);
	void saveNodeContent(Map<String, Object> map);
	void updateNodeContent(Map<String, Object> map);
	List<KgNodeDetailFile> getNodeImageList(Integer domainId, Integer nodeId);
	List<KgNodeDetail> getNodeContent(Integer domainId, Integer nodeId);
	void deleteNodeImage(Integer domainId,Integer nodeId);
}
