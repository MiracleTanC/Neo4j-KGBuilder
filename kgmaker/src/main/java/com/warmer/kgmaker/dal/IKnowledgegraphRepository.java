package com.warmer.kgmaker.dal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface IKnowledgegraphRepository {
	List<Map<String,Object>> getDomains();
	List<Map<String,Object>> getDomainList(@Param("domainname")String domainname,@Param("createuser")String createuser);
	List<Map<String,Object>> getRelationshipList(@Param("domainid")Integer domainid,@Param("relationtype")Integer relationtype,@Param("shipname")String shipname);
	void saveDomain(@Param("params") Map<String, Object> map);
	void updateDomain(@Param("params") Map<String, Object> map);
	void deleteDomain(@Param("id") Integer id);
	List<Map<String,Object>> getDomainByName(@Param("domainname") String domainname);
	List<Map<String,Object>> getDomainById(@Param("domainid")Integer domainid);

	void saveNodeImage(@Param("maplist") List<Map<String, Object>> mapList);
	void saveNodeContent(@Param("params") Map<String, Object> map);
	void updateNodeContent(@Param("params") Map<String, Object> map);
	List<Map<String,Object>> getNodeImageList(@Param("domainid") Integer domainid,@Param("nodeid") Integer nodeid);
	List<Map<String,Object>> getNodeContent(@Param("domainid") Integer domainid,@Param("nodeid") Integer nodeid);
	void deleteNodeImage(@Param("domainid") Integer domainid,@Param("nodeid") Integer nodeid);
}
