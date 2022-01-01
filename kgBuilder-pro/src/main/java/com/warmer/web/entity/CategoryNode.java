package com.warmer.web.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CategoryNode {
    /**
     *节点id
     */

    private Integer categoryNodeId;
    /**
     *节点名称
     */
    private String categoryNodeName;
    /**
     *节点编码
     */
    private String categoryNodeCode;
    /**
     *节点颜色
     */
    private String color;

    /**
     * 系统编码
     */
    private String systemCode;
    /**
     *分类id
     */
    private Long categoryId;
    /**
     *节点父id
     */
    private Integer parentId;
    /**
     *节点父编码
     */
    private String parentCode;

    /**
     * 附件uuid
     */
    private String fileUuid;
    /**
     *层数
     */
    private Integer treeLevel;
    /**
     *是否叶子节点
     */
    private Integer isLeaf;
    /**
     *状态
     */
    private Integer status;


    private String createUser;
    /**
     *
     */
    private Date createTime;
    /**
     *
     */
    private String updateUser;
    /**
     *
     */
    private Date updateTime;
}
