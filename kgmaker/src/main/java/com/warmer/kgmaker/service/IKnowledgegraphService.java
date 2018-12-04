package com.warmer.kgmaker.service;


import java.util.List;
import java.util.Map;

public interface IKnowledgegraphService {
	List<Map<String,Object>> getDomains();
	List<Map<String,Object>> getDomainList(String domainname,String createuser);
	void saveDomain(Map<String, Object> map);
	void updateDomain(Map<String, Object> map);
	void deleteDomain(Integer id);
	List<Map<String,Object>> getDomainByName(String domainname);
	List<Map<String,Object>> getDomainById(Integer domainid);
	void saveNodeImage(List<Map<String, Object>> mapList);
	void saveNodeContent(Map<String, Object> map);
	void updateNodeContent(Map<String, Object> map);
	List<Map<String,Object>> getNodeImageList(Integer domainid,Integer nodeid);
	List<Map<String,Object>> getNodeContent(Integer domainid,Integer nodeid);
	void deleteNodeImage(Integer domainid,Integer nodeid);
}
