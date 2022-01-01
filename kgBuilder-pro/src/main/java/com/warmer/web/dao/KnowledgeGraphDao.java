package com.warmer.web.dao;

import com.warmer.web.entity.KgDomain;
import com.warmer.web.entity.KgNodeDetail;
import com.warmer.web.entity.KgNodeDetailFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface KnowledgeGraphDao {
	List<KgDomain> getDomains();
	List<KgDomain> getRecommendDomainList();
	List<KgDomain> getDomainList(@Param("domainName")String domainName, @Param("type")Integer type,@Param("commend")Integer commend);
	void saveDomain(KgDomain map);
	void updateDomain(KgDomain map);
	void deleteDomain(@Param("id") Integer id);
	List<KgDomain> getDomainByName(@Param("domainName") String domainName);
	List<KgDomain> getDomainById(@Param("domainId")Integer domainId);
	KgDomain selectById(@Param("domainId")Integer domainId);

	void saveNodeImage(@Param("maplist") List<Map<String, Object>> mapList);
	void saveNodeContent(@Param("params") Map<String, Object> map);
	void updateNodeContent(@Param("params") Map<String, Object> map);
	List<KgNodeDetailFile> getNodeImageList(@Param("domainId") Integer domainId, @Param("nodeId") Integer nodeId);
	List<KgNodeDetail> getNodeContent(@Param("domainId") Integer domainId, @Param("nodeId") Integer nodeId);
	void deleteNodeImage(@Param("domainId") Integer domainId,@Param("nodeId") Integer nodeId);
}
