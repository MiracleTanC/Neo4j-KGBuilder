package com.warmer.web.dao;

import com.warmer.web.entity.CategoryNode;
import com.warmer.web.request.CategoryNodeQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 分类节点数据访问接口
 *
 * 提供分类节点的增删改查、树结构维护、系统编码初始化与更新、以及导入文件相关的批量操作。
 */
public interface CategoryNodeDao {
    /**
     * 根据主键删除分类节点
     * @param id 主键ID
     * @return 受影响行数
     */
    int deleteByPrimaryKey(Integer id);
    /**
     * 插入单个分类节点
     * @param record 节点实体
     * @return 受影响行数
     */
    int insert(CategoryNode record);
    /**
     * 批量插入分类节点
     * @param records 节点实体列表
     * @return 受影响行数
     */
    int batchInsert(List<CategoryNode> records);
    /**
     * 批量更新表达式字段
     * @param records 节点实体列表
     */
    void batchUpdateExpression(List<CategoryNode> records);
    /**
     * 根据主键查询分类节点
     * @param categoryNodeId 节点ID
     * @return 分类节点实体
     */
    CategoryNode selectByPrimaryKey(@Param("categoryNodeId")Integer categoryNodeId);
    /**
     * 根据主键更新分类节点
     * @param record 节点实体
     * @return 受影响行数
     */
    int updateByPrimaryKey(CategoryNode record);
    /**
     * 重命名分类节点
     * @param categoryNodeId 节点ID
     * @param categoryNodeName 新名称
     * @return 受影响行数
     */
    int reName(@Param("categoryNodeId")Integer categoryNodeId,@Param("categoryNodeName")String  categoryNodeName);
    /**
     * 更新系统编码
     * @param categoryNodeId 节点ID
     * @param systemCode 系统编码
     * @return 受影响行数
     */
    int updateCodeByPrimaryKey(@Param("categoryNodeId")Integer categoryNodeId,@Param("systemCode")String systemCode);
    /**
     * 初始化系统编码（基于分类与文件UUID）
     * @param categoryId 分类ID
     * @param fileUuid 文件UUID
     */
    void initSystemCode(@Param("categoryId")Long categoryId,@Param("fileUuid")String fileUuid);
    /**
     * 更新节点父子关系
     * @param categoryId 分类ID
     * @param fileUuid 文件UUID
     * @param categoryNodeId 节点ID
     */
    void updateNodeRelation(@Param("categoryId")Long categoryId,@Param("fileUuid")String fileUuid,@Param("categoryNodeId")Integer categoryNodeId);
    /**
     * 更新系统编码全路径
     * @param categoryId 分类ID
     * @param fileUuid 文件UUID
     */
    void updateSystemCodeFullPath(@Param("categoryId")Long categoryId,@Param("fileUuid")String fileUuid);
    /**
     * 重新计算并更新树层级
     * @param categoryId 分类ID
     */
    void updateTreeLevel(@Param("categoryId")Long categoryId);
    /**
     * 更新叶子节点状态
     * @param categoryNodeId 节点ID
     * @param isLeaf 是否叶子(1:是,0:否)
     * @return 受影响行数
     */
    int updateLeafStatusByPrimaryKey(@Param("categoryNodeId")Integer categoryNodeId,@Param("isLeaf")Integer isLeaf);
    /**
     * 条件查询分类节点列表
     * @param queryItem 查询条件
     * @return 节点列表
     */
    List<CategoryNode> queryForList(CategoryNodeQuery queryItem);
    /**
     * 查询某父节点下的直接子节点
     * @param categoryId 分类ID
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    List<CategoryNode> selectByParentId(@Param("categoryId")Long categoryId, @Param("parentId") Integer parentId);
    /**
     * 根据父节点与名称查询
     * @param categoryId 分类ID
     * @param parentId 父节点ID
     * @param categoryNodeName 节点名称
     * @return 匹配节点列表
     */
    List<CategoryNode> selectByParentIdAndName(@Param("categoryId")Long categoryId,@Param("parentId") Integer parentId,@Param("categoryNodeName") String categoryNodeName);

    /**
     * 获取当前分类的所有节点数据,使用mysql8.0递归查询
     * @param categoryId 指定分类id
     * @param categoryNodeId 起始节点ID（可选）
     * @return 当前分类的所有节点数据
     */
    List<CategoryNode> queryForTree(@Param("categoryId") Long categoryId, @Param("categoryNodeId") Integer categoryNodeId);
    List<CategoryNode> selectTreeForParent(@Param("categoryNodeId") Integer categoryNodeId);
    List<CategoryNode> selectTreeForParentBySystemCode(@Param("systemCode") String systemCode);
    List<CategoryNode> selectRecentEditNode(@Param("categoryId") Long categoryId);

    /**
     * 删除附件导入的节点
     * @param fileUuid 附件生成的uuid
     * @param systemCode 系统编码前缀
     * @return 受影响行数
     */
    int deleteNodeByFileUuid(@Param("fileUuid") String fileUuid,@Param("systemCode") String systemCode);
    /**
     * 根据系统编码左正则匹配删除节点
     * @param systemCode 系统编码前缀
     * @return 受影响行数
     */
    int deleteNodeBySystemLeftRegular(@Param("systemCode") String systemCode);
    /**
     * 根据文件UUID查询节点列表
     * @param fileUuid 文件UUID
     * @return 节点列表
     */
    List<CategoryNode> selectByFileUuid(@Param("fileUuid") String fileUuid);

}
