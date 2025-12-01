package com.warmer.web.service.impl;

import com.warmer.web.dao.CategoryNodeDao;
import com.warmer.web.entity.CategoryNode;
import com.warmer.web.model.TreeNode;
import com.warmer.web.request.CategoryNodeQuery;
import com.warmer.web.service.CategoryNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryNodeServiceImpl implements CategoryNodeService {
    @Autowired
    CategoryNodeDao categoryNodeRepository;
    /**
     * 根据主键删除节点
     * @param id 主键ID
     * @return 影响行数
     */
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return categoryNodeRepository.deleteByPrimaryKey(id);
    }

    /**
     * 插入新节点
     * @param record 节点实体
     * @return 影响行数
     */
    @Override
    public int insert(CategoryNode record) {
        return categoryNodeRepository.insert(record);
    }
    /**
     * 批量插入节点
     * @param records 节点列表
     * @return 影响行数
     */
    @Override
    public int batchInsert(List<CategoryNode> records) {
        return categoryNodeRepository.batchInsert(records);
    }

    /**
     * 批量更新表达式
     * @param records 节点列表
     */
    @Override
    public void batchUpdateExpression(List<CategoryNode> records) {
        categoryNodeRepository.batchUpdateExpression(records);
    }

    /**
     * 根据主键查询节点
     * @param id 节点ID
     * @return 节点实体
     */
    @Override
    public CategoryNode selectByPrimaryKey(Integer id) {
        return categoryNodeRepository.selectByPrimaryKey(id);
    }

    /**
     * 根据主键更新节点
     * @param record 节点实体
     * @return 影响行数
     */
    @Override
    public int updateByPrimaryKey(CategoryNode record) {
        return categoryNodeRepository.updateByPrimaryKey(record);
    }

    /**
     * 更新节点系统编码
     * @param categoryNodeId 节点ID
     * @param systemCode 系统编码
     * @return 影响行数
     */
    @Override
    public int updateCodeByPrimaryKey(Integer categoryNodeId, String systemCode) {
        return categoryNodeRepository.updateCodeByPrimaryKey(categoryNodeId, systemCode);
    }

    /**
     * 初始化系统编码
     * @param categoryId 分类ID
     * @param fileUuid 文件UUID
     */
    @Override
    public void initSystemCode(Long categoryId, String fileUuid) {
        categoryNodeRepository.initSystemCode(categoryId,fileUuid);
    }

    /**
     * 更新节点关系
     * @param categoryId 分类ID
     * @param fileUuid 文件UUID
     * @param categoryNodeId 节点ID
     */
    @Override
    public void updateNodeRelation(Long categoryId, String fileUuid, Integer categoryNodeId) {
        categoryNodeRepository.updateNodeRelation(categoryId,fileUuid,categoryNodeId);
    }

    /**
     * 更新系统编码全路径
     * @param categoryId 分类ID
     * @param fileUuid 文件UUID
     */
    @Override
    public void updateSystemCodeFullPath(Long categoryId, String fileUuid) {
        categoryNodeRepository.updateSystemCodeFullPath(categoryId,fileUuid);
    }

    /**
     * 更新树层级
     * @param categoryId 分类ID
     */
    @Override
    public void updateTreeLevel(Long categoryId) {
        categoryNodeRepository.updateTreeLevel(categoryId);
    }

    /**
     * 更新叶子节点状态
     * @param categoryNodeId 节点ID
     * @param isLeaf 是否叶子节点
     * @return 影响行数
     */
    @Override
    public int updateLeafStatusByPrimaryKey(Integer categoryNodeId, Integer isLeaf) {
        return categoryNodeRepository.updateLeafStatusByPrimaryKey(categoryNodeId, isLeaf);
    }

    /**
     * 重命名节点
     * @param categoryNodeId 节点ID
     * @param categoryNodeName 新名称
     * @return 影响行数
     */
    @Override
    public int reName(Integer categoryNodeId, String categoryNodeName) {
        return categoryNodeRepository.reName(categoryNodeId, categoryNodeName);
    }


    /**
     * 删除附件导入的节点
     * @param fileUuid 附件生成的uuid
     * @param systemCode 系统编码
     * @return 影响行数
     */
    @Override
    public int deleteNodeByFileUuid(String fileUuid,String systemCode) {
        //删除与本身节点来自同一个文件的所有子节点
        return categoryNodeRepository.deleteNodeByFileUuid(fileUuid,systemCode);
    }

    /**
     * 根据系统编码左匹配删除节点
     * @param systemCode 系统编码
     * @return 影响行数
     */
    @Override
    public int deleteNodeBySystemLeftRegular(String systemCode) {
        return categoryNodeRepository.deleteNodeBySystemLeftRegular(systemCode);
    }

    /**
     * 根据文件UUID查询节点
     * @param fileUuid 文件UUID
     * @return 节点列表
     */
    @Override
    public List<CategoryNode> selectByFileUuid(String fileUuid) {
        return categoryNodeRepository.selectByFileUuid(fileUuid);
    }

    /**
     * 查询节点列表
     * @param queryItem 查询条件
     * @return 节点列表
     */
    @Override
    public List<CategoryNode> queryForList(CategoryNodeQuery queryItem) {
        return categoryNodeRepository.queryForList(queryItem);
    }

    /**
     * 根据父ID查询子节点
     * @param categoryId 分类ID
     * @param parentId 父节点ID
     * @return 树节点列表
     */
    @Override
    public List<TreeNode> selectByParentId(Long categoryId, Integer parentId) {
        List<CategoryNode> nodeData = categoryNodeRepository.selectByParentId(categoryId, parentId);
        List<TreeNode> item = new ArrayList<>();
        for (CategoryNode cate : nodeData) {
            TreeNode cateModel = new TreeNode();
            cateModel.setId(cate.getCategoryNodeId());
            cateModel.setLabel(cate.getCategoryNodeName());
            cateModel.setParentId(cate.getParentId());
            cateModel.setTreeLevel(cateModel.getTreeLevel());
            //添加额外的属性
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("categoryId", cate.getCategoryId());
            attributes.put("categoryNodeCode", cate.getCategoryNodeCode());
            attributes.put("systemCode", cate.getSystemCode());
            cateModel.setAttributes(attributes);
            cateModel.setLeaf(cate.getIsLeaf() == 1);
            item.add(cateModel);
        }
        return item;
    }

    /**
     * 根据父ID和名称查询节点
     * @param categoryId 分类ID
     * @param parentId 父节点ID
     * @param categoryNodeName 节点名称
     * @return 节点列表
     */
    @Override
    public List<CategoryNode> selectByParentIdAndName(Long categoryId, Integer parentId, String categoryNodeName) {
        return categoryNodeRepository.selectByParentIdAndName(categoryId,parentId,categoryNodeName);
    }

    /**
     * 获取当前分类的所有节点数据
     * @param categoryId 指定分类id
     * @param categoryNodeId 节点ID
     * @return 当前分类的所有节点数据
     */
    @Override
    public List<CategoryNode> queryForTree(Long categoryId, Integer categoryNodeId) {
        return categoryNodeRepository.queryForTree(categoryId,categoryNodeId);
    }

    /**
     * 根据父节点ID查询子节点树
     * @param categoryNodeId 父节点ID
     * @return 节点列表
     */
    @Override
    public List<CategoryNode> selectTreeForParent(Integer categoryNodeId) {
        return categoryNodeRepository.selectTreeForParent(categoryNodeId);
    }

    /**
     * 根据系统编码查询子节点树
     * @param systemCode 系统编码
     * @return 节点列表
     */
    @Override
    public List<CategoryNode> selectTreeForParentBySystemCode(String systemCode) {
        return categoryNodeRepository.selectTreeForParentBySystemCode(systemCode);
    }

    /**
     * 查询最近编辑的节点
     * @param categoryId 分类ID
     * @return 节点列表
     */
    @Override
    public List<CategoryNode> selectRecentEditNode(Long categoryId) {
        return categoryNodeRepository.selectRecentEditNode(categoryId);
    }

    /**
     * 获取树形数据
     * @param categoryId 分类ID
     * @param categoryNodeId 节点ID
     * @return 树节点列表
     */
    @Override
    public List<TreeNode> getTreeData(Long categoryId, Integer categoryNodeId) {
        List<CategoryNode> CategoryNodes = categoryNodeRepository.queryForTree(categoryId, categoryNodeId);
        return getTree(categoryNodeId, CategoryNodes);
    }

    /**
     * 递归构建树
     * @param parentId 父节点ID
     * @param nodeList 节点列表
     * @return 树节点列表
     */
    private List<TreeNode> getTree(int parentId, List<CategoryNode> nodeList) {
        List<TreeNode> item = new ArrayList<>();
        Iterator<CategoryNode> treeList = nodeList.stream().filter(m -> m.getParentId() == parentId).iterator();
        while (treeList.hasNext()) {
            CategoryNode cate = treeList.next();
            TreeNode cateModel = new TreeNode();
            cateModel.setId(cate.getCategoryNodeId());
            cateModel.setLabel(cate.getCategoryNodeName());
            cateModel.setParentId(cate.getParentId());
            cateModel.setTreeLevel(cateModel.getTreeLevel());
            //添加额外的属性
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("categoryId", cate.getCategoryId());
            attributes.put("categoryNodeCode", cate.getCategoryNodeCode());
            cateModel.setAttributes(attributes);
            List<TreeNode> childrenList = getTree(cate.getCategoryNodeId(), nodeList);
            if (!childrenList.isEmpty()) {
                cateModel.setChildren(childrenList);
            }
            cateModel.setLeaf(childrenList.isEmpty());
            item.add(cateModel);
        }
        return item;
    }

}
