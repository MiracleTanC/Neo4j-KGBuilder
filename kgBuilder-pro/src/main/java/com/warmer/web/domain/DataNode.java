package com.warmer.web.domain;

import com.warmer.web.request.GraphNodeItem;
import lombok.Data;

import java.util.List;
import java.util.Map;
/**
 * 数据分析流程化配置中的节点数据存放对象
 */
@Data
public class DataNode {
    /**
     * 组件id
     */
	private String id;
	private String domain;

    private String nodeName;

    /**
     * 组件类型code：调用相关的服务
     */
    private String nodeCode;

    /**
     *  组件数据json
     */
    private GraphNodeItem data;

    /**
     * 组件输入：所有指向当前节点的上一组件id集合
     */
    private List<DataNode> prevNodes;
    /**
     * 组件输出：当前节点的下一组件id集合
     */
    private List<DataNode> nextNodes;


    private boolean isStart;

    private boolean isExecuted;
    private boolean isValid;

}
