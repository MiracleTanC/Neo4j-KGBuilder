package com.warmer.web.service;

import com.warmer.web.entity.CategoryNode;
import com.warmer.web.model.TreeNode;
import com.warmer.web.request.CategoryNodeQuery;

import java.util.List;


/**
 * 分类节点业务服务接口
 *
 * 管理分类树节点的增删改查、系统编码初始化/维护、树层级计算、
 * 以及附件导入场景下的批量清理与查询。
 */
public interface CategoryNodeService {
    /**
     * 根据主键删除节点
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入新节点
     * @param record 节点实体
     * @return 影响行数
     */
    int insert(CategoryNode record);

    /**
     * 批量插入节点
     * @param records 节点列表
     * @return 影响行数
     */
    int batchInsert(List<CategoryNode> records);

    /**
     * 批量更新表达式
     * @param records 节点列表
     */
    void batchUpdateExpression(List<CategoryNode> records);

    /**
     * 根据主键查询节点
     * @param categoryNodeId 节点ID
     * @return 节点实体
     */
    CategoryNode selectByPrimaryKey(Integer categoryNodeId);

    /**
     * 根据主键更新节点
     * @param record 节点实体
     * @return 影响行数
     */
    int updateByPrimaryKey(CategoryNode record);

    /**
     * 重命名节点
     * @param categoryNodeId 节点ID
     * @param categoryNodeName 新名称
     * @return 影响行数
     */
    int reName(Integer categoryNodeId,String  categoryNodeName);

    /**
     * 更新节点系统编码
     * @param categoryNodeId 节点ID
     * @param systemCode 系统编码
     * @return 影响行数
     */
    int updateCodeByPrimaryKey(Integer categoryNodeId,String systemCode);

    /**
     * 初始化系统编码
     * @param categoryId 分类ID
     * @param fileUuid 文件UUID
     */
    void initSystemCode(Long categoryId,String fileUuid);

    /**
     * 更新节点关系
     * @param categoryId 分类ID
     * @param fileUuid 文件UUID
     * @param categoryNodeId 节点ID
     */
    void updateNodeRelation(Long categoryId,String fileUuid,Integer categoryNodeId);

    /**
     * 更新系统编码全路径
     * @param categoryId 分类ID
     * @param fileUuid 文件UUID
     */
    void updateSystemCodeFullPath(Long categoryId,String fileUuid);

    /**
     * 更新树层级
     * @param categoryId 分类ID
     */
    void updateTreeLevel(Long categoryId);

    /**
     * 更新叶子节点状态
     * @param categoryNodeId 节点ID
     * @param isLeaf 是否叶子节点
     * @return 影响行数
     */
    int updateLeafStatusByPrimaryKey(Integer categoryNodeId,Integer isLeaf);

    /**
     * 查询节点列表
     * @param queryItem 查询条件
     * @return 节点列表
     */
    List<CategoryNode> queryForList(CategoryNodeQuery queryItem);

    /**
     * 根据父ID查询子节点
     * @param categoryId 分类ID
     * @param parentId 父节点ID
     * @return 树节点列表
     */
    List<TreeNode> selectByParentId(Long categoryId, Integer parentId);

    /**
     * 根据父ID和名称查询节点
     * @param categoryId 分类ID
     * @param parentId 父节点ID
     * @param categoryNodeName 节点名称
     * @return 节点列表
     */
    List<CategoryNode> selectByParentIdAndName(Long categoryId,Integer parentId,String categoryNodeName);

    /**
     * 获取当前分类的所有节点数据,使用mysql8.0递归查询
     * @param categoryId 指定分类id
     * @return 当前分类的所有节点数据
     */
    List<CategoryNode> queryForTree( Long categoryId,  Integer categoryNodeId);
    /**
     * 根据父节点ID查询子节点树
     * @param categoryNodeId 父节点ID
     * @return 节点列表
     */
    List<CategoryNode> selectTreeForParent( Integer categoryNodeId);

    /**
     * 根据系统编码查询子节点树
     * @param systemCode 系统编码
     * @return 节点列表
     */
    List<CategoryNode> selectTreeForParentBySystemCode( String systemCode);

    /**
     * 查询最近编辑的节点
     * @param categoryId 分类ID
     * @return 节点列表
     */
    List<CategoryNode> selectRecentEditNode( Long categoryId);

    /**
     * 获取树形数据
     * @param categoryId 分类ID
     * @param categoryNodeId 节点ID
     * @return 树节点列表
     */
    List<TreeNode> getTreeData(Long categoryId, Integer categoryNodeId);
    /**
     * 删除附件导入的节点
     * @param fileUuid 附件生成的uuid
     * @param systemCode 系统编码
     * @return 影响行数
     */
    int deleteNodeByFileUuid( String fileUuid, String systemCode);

    /**
     * 根据系统编码左匹配删除节点
     * @param systemCode 系统编码
     * @return 影响行数
     */
    int deleteNodeBySystemLeftRegular( String systemCode);

    /**
     * 根据文件UUID查询节点
     * @param fileUuid 文件UUID
     * @return 节点列表
     */
    List<CategoryNode> selectByFileUuid( String fileUuid);
}
