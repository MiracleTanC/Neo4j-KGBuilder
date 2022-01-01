package com.warmer.web.dao;

import com.warmer.web.entity.CategoryNode;
import com.warmer.web.request.CategoryNodeQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryNodeDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CategoryNode record);

    int batchInsert(List<CategoryNode> records);

    void batchUpdateExpression(List<CategoryNode> records);

    CategoryNode selectByPrimaryKey(@Param("categoryNodeId")Integer categoryNodeId);

    int updateByPrimaryKey(CategoryNode record);

    int reName(@Param("categoryNodeId")Integer categoryNodeId,@Param("categoryNodeName")String  categoryNodeName);

    int updateCodeByPrimaryKey(@Param("categoryNodeId")Integer categoryNodeId,@Param("systemCode")String systemCode);

    void initSystemCode(@Param("categoryId")Long categoryId,@Param("fileUuid")String fileUuid);

    void updateNodeRelation(@Param("categoryId")Long categoryId,@Param("fileUuid")String fileUuid,@Param("categoryNodeId")Integer categoryNodeId);

    void updateSystemCodeFullPath(@Param("categoryId")Long categoryId,@Param("fileUuid")String fileUuid);

    void updateTreeLevel(@Param("categoryId")Long categoryId);

    int updateLeafStatusByPrimaryKey(@Param("categoryNodeId")Integer categoryNodeId,@Param("isLeaf")Integer isLeaf);

    List<CategoryNode> queryForList(CategoryNodeQuery queryItem);

    List<CategoryNode> selectByParentId(@Param("categoryId")Long categoryId, @Param("parentId") Integer parentId);

    List<CategoryNode> selectByParentIdAndName(@Param("categoryId")Long categoryId,@Param("parentId") Integer parentId,@Param("categoryNodeName") String categoryNodeName);

    /**
     * 获取当前分类的所有节点数据,使用mysql8.0递归查询
     * @param categoryId 指定分类id
     * @return 当前分类的所有节点数据
     */
    List<CategoryNode> queryForTree(@Param("categoryId") Long categoryId, @Param("categoryNodeId") Integer categoryNodeId);
    List<CategoryNode> selectTreeForParent(@Param("categoryNodeId") Integer categoryNodeId);
    List<CategoryNode> selectTreeForParentBySystemCode(@Param("systemCode") String systemCode);
    List<CategoryNode> selectRecentEditNode(@Param("categoryId") Long categoryId);

    /**
     * 删除附件导入的节点
     * @param fileUuid 附件生成的uuid
     */
    int deleteNodeByFileUuid(@Param("fileUuid") String fileUuid,@Param("systemCode") String systemCode);

    int deleteNodeBySystemLeftRegular(@Param("systemCode") String systemCode);

    List<CategoryNode> selectByFileUuid(@Param("fileUuid") String fileUuid);

}
