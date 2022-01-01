package com.warmer.web.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * kg_graph_node
 * @author 
 */
@Data
public class KgGraphNode implements Serializable {
    /**
     * 主键
     */
    private Long nodeId;

    /**
     * 节点唯一标识
     */
    private String nodeKey;

    /**
     * 数据表id
     */
    private Integer tableId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     */
    private String type;

    /**
     * 节点左位置
     */
    private String left;

    /**
     * 节点右位置
     */
    private String top;

    /**
     * 节点图标
     */
    private String ico;

    /**
     * 节点状态
     */
    private String state;

    /**
     * 是否可以拖动，1=是，0=否
     */
    private Integer viewOnly;

    /**
     * 数据源id
     */
    private Integer sourceId;

    /**
     * 领域id
     */
    private Integer domainId;

    /**
     * 是否是起点
     */
    private Integer startNode;

    private static final long serialVersionUID = 1L;
}