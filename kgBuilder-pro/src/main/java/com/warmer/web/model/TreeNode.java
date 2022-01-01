package com.warmer.web.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class TreeNode {
    /**
     * 节点key值，可自由组合，需保证唯一
     */
    private String key;
    /**
     * 节点ID
     */
    private Integer id;

    /**
     * 节点Level
     */
    private Integer treeLevel;
    /**
     * 显示节点文本
     */
    private String label;
    /**
     * 节点状态，open=1 closed=0
     */
    private Integer state;

    /**
     * 节点是否被选中 true false
     */
    private boolean checked = false;
    /**
     * 节点属性
     */
    private Map<String, Object> attributes;

    /**
     * 节点的子节点
     */
    private List<TreeNode> children = new ArrayList<TreeNode>();

    /**
     * 父ID
     */
    private Integer parentId;

    /**
     * 父节点唯一标识，自由组合，保证唯一
     */
    private String parentKey;

    /**
     * 是否是叶子节点
     */
    private boolean isLeaf = false;


}
