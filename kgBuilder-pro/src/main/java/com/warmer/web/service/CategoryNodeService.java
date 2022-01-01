package com.warmer.web.service;

import com.warmer.web.entity.CategoryNode;
import com.warmer.web.model.TreeNode;
import com.warmer.web.request.CategoryNodeQuery;

import java.util.List;


public interface CategoryNodeService {
    int deleteByPrimaryKey(Integer id);

    int insert(CategoryNode record);

    int batchInsert(List<CategoryNode> records);

    void batchUpdateExpression(List<CategoryNode> records);

    CategoryNode selectByPrimaryKey(Integer categoryNodeId);

    int updateByPrimaryKey(CategoryNode record);

    int reName(Integer categoryNodeId,String  categoryNodeName);

    int updateCodeByPrimaryKey(Integer categoryNodeId,String systemCode);

    void initSystemCode(Long categoryId,String fileUuid);

    void updateNodeRelation(Long categoryId,String fileUuid,Integer categoryNodeId);

    void updateSystemCodeFullPath(Long categoryId,String fileUuid);

    void updateTreeLevel(Long categoryId);

    int updateLeafStatusByPrimaryKey(Integer categoryNodeId,Integer isLeaf);

    List<CategoryNode> queryForList(CategoryNodeQuery queryItem);

    List<TreeNode> selectByParentId(Long categoryId, Integer parentId);

    List<CategoryNode> selectByParentIdAndName(Long categoryId,Integer parentId,String categoryNodeName);

    /**
     * 获取当前分类的所有节点数据,使用mysql8.0递归查询
     * @param categoryId 指定分类id
     * @return 当前分类的所有节点数据
     */
    List<CategoryNode> queryForTree( Long categoryId,  Integer categoryNodeId);
    List<CategoryNode> selectTreeForParent( Integer categoryNodeId);
    List<CategoryNode> selectTreeForParentBySystemCode( String systemCode);
    List<CategoryNode> selectRecentEditNode( Long categoryId);
    List<TreeNode> getTreeData(Long categoryId, Integer categoryNodeId);
    /**
     * 删除附件导入的节点
     * @param fileUuid 附件生成的uuid
     */
    int deleteNodeByFileUuid( String fileUuid, String systemCode);

    int deleteNodeBySystemLeftRegular( String systemCode);

    List<CategoryNode> selectByFileUuid( String fileUuid);
}
