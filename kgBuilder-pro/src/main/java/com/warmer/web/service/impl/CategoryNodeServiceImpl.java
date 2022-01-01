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
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return categoryNodeRepository.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(CategoryNode record) {
        return categoryNodeRepository.insert(record);
    }
    @Override
    public int batchInsert(List<CategoryNode> records) {
        return categoryNodeRepository.batchInsert(records);
    }

    @Override
    public void batchUpdateExpression(List<CategoryNode> records) {
        categoryNodeRepository.batchUpdateExpression(records);
    }

    @Override
    public CategoryNode selectByPrimaryKey(Integer id) {
        return categoryNodeRepository.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKey(CategoryNode record) {
        return categoryNodeRepository.updateByPrimaryKey(record);
    }

    @Override
    public int updateCodeByPrimaryKey(Integer categoryNodeId, String systemCode) {
        return categoryNodeRepository.updateCodeByPrimaryKey(categoryNodeId, systemCode);
    }

    @Override
    public void initSystemCode(Long categoryId, String fileUuid) {
        categoryNodeRepository.initSystemCode(categoryId,fileUuid);
    }

    @Override
    public void updateNodeRelation(Long categoryId, String fileUuid, Integer categoryNodeId) {
        categoryNodeRepository.updateNodeRelation(categoryId,fileUuid,categoryNodeId);
    }

    @Override
    public void updateSystemCodeFullPath(Long categoryId, String fileUuid) {
        categoryNodeRepository.updateSystemCodeFullPath(categoryId,fileUuid);
    }

    @Override
    public void updateTreeLevel(Long categoryId) {
        categoryNodeRepository.updateTreeLevel(categoryId);
    }

    @Override
    public int updateLeafStatusByPrimaryKey(Integer categoryNodeId, Integer isLeaf) {
        return categoryNodeRepository.updateLeafStatusByPrimaryKey(categoryNodeId, isLeaf);
    }

    @Override
    public int reName(Integer categoryNodeId, String categoryNodeName) {
        return categoryNodeRepository.reName(categoryNodeId, categoryNodeName);
    }


    @Override
    public int deleteNodeByFileUuid(String fileUuid,String systemCode) {
        //删除与本身节点来自同一个文件的所有子节点
        return categoryNodeRepository.deleteNodeByFileUuid(fileUuid,systemCode);
    }

    @Override
    public int deleteNodeBySystemLeftRegular(String systemCode) {
        return categoryNodeRepository.deleteNodeBySystemLeftRegular(systemCode);
    }

    @Override
    public List<CategoryNode> selectByFileUuid(String fileUuid) {
        return categoryNodeRepository.selectByFileUuid(fileUuid);
    }

    @Override
    public List<CategoryNode> queryForList(CategoryNodeQuery queryItem) {
        return categoryNodeRepository.queryForList(queryItem);
    }

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

    @Override
    public List<CategoryNode> selectByParentIdAndName(Long categoryId, Integer parentId, String categoryNodeName) {
        return categoryNodeRepository.selectByParentIdAndName(categoryId,parentId,categoryNodeName);
    }

    @Override
    public List<CategoryNode> queryForTree(Long categoryId, Integer categoryNodeId) {
        return categoryNodeRepository.queryForTree(categoryId,categoryNodeId);
    }

    @Override
    public List<CategoryNode> selectTreeForParent(Integer categoryNodeId) {
        return categoryNodeRepository.selectTreeForParent(categoryNodeId);
    }

    @Override
    public List<CategoryNode> selectTreeForParentBySystemCode(String systemCode) {
        return categoryNodeRepository.selectTreeForParentBySystemCode(systemCode);
    }

    @Override
    public List<CategoryNode> selectRecentEditNode(Long categoryId) {
        return categoryNodeRepository.selectRecentEditNode(categoryId);
    }

    @Override
    public List<TreeNode> getTreeData(Long categoryId, Integer categoryNodeId) {
        List<CategoryNode> CategoryNodes = categoryNodeRepository.queryForTree(categoryId, categoryNodeId);
        return getTree(categoryNodeId, CategoryNodes);
    }

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
