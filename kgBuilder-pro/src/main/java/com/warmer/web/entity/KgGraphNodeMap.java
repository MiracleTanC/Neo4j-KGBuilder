package com.warmer.web.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * kg_graph_node_map
 * @author 
 */
@Data
public class KgGraphNodeMap implements Serializable {
    private Long id;

    /**
     * 列id
     */
    private Integer columnId;

    /**
     * 列图标
     */
    private String ico;

    /**
     * 是否主键
     */
    private Integer isPrimary;
    /**
     * 主实体
     */
    private Integer isMainEntity;

    /**
     * 数据列key
     */
    private String itemId;

    /**
     * 原始数据列字段
     */
    private String itemCode;

    /**
     * 列别名
     */
    private String itemName;

    /**
     * 字段类型
     */
    private String itemType;

    /**
     * 节点id
     */
    private Long nodeId;
    /**
     * 领域id
     */
    private Integer domainId;

    private static final long serialVersionUID = 1L;
}