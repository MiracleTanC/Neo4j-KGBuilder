package com.warmer.web.service.impl;

import com.warmer.base.util.DateUtil;
import com.warmer.web.dao.KnowledgeGraphDao;
import com.warmer.web.entity.KgDomain;
import com.warmer.web.entity.KgNodeDetail;
import com.warmer.web.entity.KgNodeDetailFile;
import com.warmer.web.service.KGManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KGManagerServiceImpl implements KGManagerService {

    @Autowired
    KnowledgeGraphDao knowledgeGraphDao;

    /**
     * 获取所有领域列表
     *
     * @return List<KgDomain> 领域列表
     */
    @Override
    public List<KgDomain> getDomains() {
        return knowledgeGraphDao.getDomains();
    }

    /**
     * 分页查询领域列表
     *
     * @param domainName 领域名称（模糊查询）
     * @param type       类型
     * @param commend    推荐状态
     * @return List<KgDomain> 领域列表
     */
    @Override
    public List<KgDomain> getDomainList(String domainName,Integer type,Integer commend) {
        return knowledgeGraphDao.getDomainList(domainName,type,commend);
    }

    /**
     * 保存领域信息
     * <p>
     * 自动设置创建时间和修改时间为当前时间。
     * </p>
     *
     * @param map 领域对象
     * @return Integer 保存后的领域ID
     */
    @Override
    public Integer saveDomain(KgDomain map) {
        map.setCreateTime(DateUtil.getDateNow());
        map.setModifyTime(DateUtil.getDateNow());
        knowledgeGraphDao.saveDomain(map);
        return map.getId();
    }

    /**
     * 快速创建领域
     * <p>
     * 创建一个带有默认配置的领域对象。
     * </p>
     *
     * @param domain     领域标签
     * @param domainAlia 领域名称（别名）
     * @param type       类型
     * @return Integer 新建领域的ID
     */
    @Override
    public Integer quickCreateDomain(String domain,String domainAlia,Integer type) {
        KgDomain item = new KgDomain();
        item.setName(domainAlia);
        item.setLabel(domain);
        item.setNodeCount(0);
        item.setShipCount(0);
        item.setCreateUser("tc");
        item.setCommend(0);
        item.setType(type);
        item.setStatus(1);
        return  saveDomain(item);
    }

    /**
     * 更新领域信息
     *
     * @param map 领域对象
     */
    @Override
    public void updateDomain(KgDomain map) {
        knowledgeGraphDao.updateDomain(map);
    }

    /**
     * 删除领域
     *
     * @param id 领域ID
     */
    @Override
    public void deleteDomain(Integer id) {
        knowledgeGraphDao.deleteDomain(id);
    }

    /**
     * 根据名称获取领域列表
     *
     * @param domainName 领域名称
     * @return List<KgDomain> 领域列表
     */
    @Override
    public List<KgDomain> getDomainByName(String domainName) {
        return knowledgeGraphDao.getDomainByName(domainName);
    }

    /**
     * 根据标签获取领域
     *
     * @param label 领域标签
     * @return KgDomain 领域对象
     */
    @Override
    public KgDomain getDomainByLabel(String label) {
        return knowledgeGraphDao.getDomainByLabel(label);
    }

    /**
     * 根据ID获取领域列表（通常只返回一个）
     *
     * @param domainId 领域ID
     * @return List<KgDomain> 领域列表
     */
    @Override
    public List<KgDomain> getDomainById(Integer domainId) {
        return knowledgeGraphDao.getDomainById(domainId);
    }

    /**
     * 根据ID查询单个领域
     *
     * @param domainId 领域ID
     * @return KgDomain 领域对象
     */
    @Override
    public KgDomain selectById(Integer domainId) {
        return knowledgeGraphDao.selectById(domainId);
    }

    /**
     * 保存节点图片信息
     *
     * @param mapList 图片信息列表
     */
    @Override
    public void saveNodeImage(List<Map<String, Object>> mapList) {
        knowledgeGraphDao.saveNodeImage(mapList);
    }

    /**
     * 保存节点富文本内容
     *
     * @param map 内容信息
     */
    @Override
    public void saveNodeContent(Map<String, Object> map) {
        knowledgeGraphDao.saveNodeContent(map);
    }

    /**
     * 更新节点富文本内容
     *
     * @param map 内容信息
     */
    @Override
    public void updateNodeContent(Map<String, Object> map) {
        knowledgeGraphDao.updateNodeContent(map);
    }

    /**
     * 获取节点图片列表
     *
     * @param domainId 领域ID
     * @param nodeId   节点ID
     * @return List<KgNodeDetailFile> 图片列表
     */
    @Override
    public List<KgNodeDetailFile> getNodeImageList(Integer domainId, Integer nodeId) {
        return knowledgeGraphDao.getNodeImageList(domainId,nodeId);
    }

    /**
     * 获取节点富文本内容
     *
     * @param domainId 领域ID
     * @param nodeId   节点ID
     * @return List<KgNodeDetail> 内容列表
     */
    @Override
    public List<KgNodeDetail> getNodeContent(Integer domainId, Integer nodeId) {
        return knowledgeGraphDao.getNodeContent(domainId,nodeId);
    }

    /**
     * 删除节点图片
     *
     * @param domainId 领域ID
     * @param nodeId   节点ID
     */
    @Override
    public void deleteNodeImage(Integer domainId, Integer nodeId) {
        knowledgeGraphDao.deleteNodeImage(domainId,nodeId);
    }
}
