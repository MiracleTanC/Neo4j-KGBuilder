package com.warmer.web.dao;

import com.warmer.web.entity.KgDomain;
import com.warmer.web.entity.KgNodeDetail;
import com.warmer.web.entity.KgNodeDetailFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
/**
 * 领域与节点内容数据访问接口
 *
 * 管理领域（Domain）的查询与增删改，以及节点内容与图片的存取。
 */
public interface KnowledgeGraphDao {
    /**
     * 获取所有领域列表
     * @return 领域列表
     */
    List<KgDomain> getDomains();

    /**
     * 按条件分页查询领域列表
     * @param domainName 领域名称（模糊）
     * @param type 领域类型
     * @param commend 是否推荐
     * @return 领域列表
     */
    List<KgDomain> getDomainList(@Param("domainName")String domainName, @Param("type")Integer type,@Param("commend")Integer commend);

    /**
     * 新增领域
     * @param map 领域实体
     */
    void saveDomain(KgDomain map);

    /**
     * 更新领域
     * @param map 领域实体
     */
    void updateDomain(KgDomain map);

    /**
     * 删除领域
     * @param id 领域ID
     */
    void deleteDomain(@Param("id") Integer id);

    /**
     * 根据名称查询领域
     * @param domainName 领域名称
     * @return 领域列表
     */
    List<KgDomain> getDomainByName(@Param("domainName") String domainName);

    /**
     * 根据标签查询领域
     * @param label 标签
     * @return 领域实体
     */
    KgDomain getDomainByLabel(@Param("label") String label);

    /**
     * 根据ID查询领域列表
     * @param domainId 领域ID
     * @return 领域列表
     */
    List<KgDomain> getDomainById(@Param("domainId")Integer domainId);

    /**
     * 根据ID查询单个领域
     * @param domainId 领域ID
     * @return 领域实体
     */
    KgDomain selectById(@Param("domainId")Integer domainId);

    /**
     * 批量保存节点图片信息
     * @param mapList 图片信息列表
     */
    void saveNodeImage(@Param("maplist") List<Map<String, Object>> mapList);

    /**
     * 保存节点富文本内容
     * @param map 内容参数
     */
    void saveNodeContent(@Param("params") Map<String, Object> map);

    /**
     * 更新节点富文本内容
     * @param map 内容参数
     */
    void updateNodeContent(@Param("params") Map<String, Object> map);

    /**
     * 查询节点图片列表
     * @param domainId 领域ID
     * @param nodeId 节点ID
     * @return 图片列表
     */
    List<KgNodeDetailFile> getNodeImageList(@Param("domainId") Integer domainId, @Param("nodeId") Integer nodeId);

    /**
     * 查询节点富文本内容
     * @param domainId 领域ID
     * @param nodeId 节点ID
     * @return 内容列表
     */
    List<KgNodeDetail> getNodeContent(@Param("domainId") Integer domainId, @Param("nodeId") Integer nodeId);

    /**
     * 删除节点图片
     * @param domainId 领域ID
     * @param nodeId 节点ID
     */
    void deleteNodeImage(@Param("domainId") Integer domainId,@Param("nodeId") Integer nodeId);
}
