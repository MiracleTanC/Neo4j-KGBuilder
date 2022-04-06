package com.warmer.web.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * DataNode 构成的 map
 *  key: 组件DataNode 的id
 *  value: 组件DataNode 对象
 */
@Data
public class ComponentContainer {
    HashMap<String, DataNode> dataNodeList;
    List<DataLink> dataLinkList;
    /**
     * 构造方法
     */
    public ComponentContainer() {
        this.dataNodeList = new HashMap<>();
        this.dataLinkList = new ArrayList<>();
    }

    /**
     * 添加DataNode 键值对
     * @param dataNode
     */
    public void addNode(DataNode dataNode) {
        this.dataNodeList.put(dataNode.getId(), dataNode);
    }

    /**
     * 获取map中的所有DataNode, 即全部map 中所有 value
     * @return List<DataNode>
     */
    public List<DataNode> getNodes() {
        return new ArrayList<>(this.dataNodeList.values());
    }


    /**
     * 根据key(nodeId), 把value(DataNode) 标记为 executedNode
     */
    public void markExecutedDone(String nodeId) {
        this.dataNodeList.get(nodeId).setExecuted(true);
    }

    /**
     * 标记该DataNode 为 executedNode
     */
    public void markExecutedDone(DataNode dataNode) {
        String nodeId = dataNode.getId();
        this.markExecutedDone(nodeId);
    }
    public void markValidDone(DataNode dataNode) {
        //String nodeId = dataNode.getId();
        //this.dataNodeList.get(nodeId).setValid(true);
        dataNode.setValid(true);
    }
    /**
     * 获取startNode list
     */
    public List<DataNode> getStartNodes() {
        List<DataNode> startNodes=new ArrayList<>();
        for (String key : this.dataNodeList.keySet()) {
            List<String> sourceIds = this.getTargetIds();
            boolean isStart = !sourceIds.contains(key);
            if (isStart) {
                DataNode dataNode = this.dataNodeList.get(key);
                dataNode.setStart(true);
                startNodes.add(dataNode);
            }
        }
        return startNodes;
    }

    /**
     * 根据nodeId 获取 DataNode
     */
    public DataNode getDataNodeByNodeId(String nodeId) {
        return this.dataNodeList.get(nodeId);
    }
    /**
     * 根据nodeId list 获取 DataNode list
     * @param list
     * @return List<DataNode>
     */
    public List<DataNode> getNodesByList(List<String> list) {
        List<DataNode> nodeList = new ArrayList<>();
        list.forEach(nodeId -> {
            DataNode node = this.dataNodeList.get(nodeId);
            nodeList.add(node);
        });
        return nodeList;
    }

    /**
     * 判断当前节点所有一度前置节点是否全部执行完毕
     * @param nodeId 节点id
     * @return true 一度前置节点全部执行完毕
     *         false 一度前置节点未执行完毕
     */
    public boolean isPreNodesExecuted(String nodeId) {
        DataNode currentNode = this.dataNodeList.get(nodeId);
        List<DataNode> preNodes = currentNode.getPrevNodes();
        return preNodes.stream().allMatch(DataNode::isExecuted);
    }
    public boolean isPreNodesValid(String nodeId) {
        DataNode currentNode = this.dataNodeList.get(nodeId);
        List<DataNode> preNodes = currentNode.getPrevNodes();
        return preNodes.stream().allMatch(DataNode::isValid);
    }
    /**
     * 所有节点是否全部执行完毕
     * @return true 流程节点全部执行完毕
     *         false 流程未执行完毕
     */
    public boolean isAllExecuted() {
        return this.getNodes().stream().allMatch(DataNode::isExecuted);
    }

    public boolean isAllValid() {
        return this.getNodes().stream().allMatch(DataNode::isValid);
    }

    /**
     *添加link元素到linkList
     * @param dataLink
     */
    public void addLink(DataLink dataLink) {
        this.dataLinkList.add(dataLink);
    }


    /**
     * 获取当前link list中所有target节点的id,及除起始节点外的所有节点
     * 如 a->b b->c b->d
     * 则    b    c    d
     * 的id会被返回
     */
    public List<String> getTargetIds() {
        List<String> list = new ArrayList<>();
        this.dataLinkList.forEach(link -> {
            String targetId = link.getTargetId();
            if ( !list.contains(targetId) ) {
                list.add(targetId);
            }
        });
        return list;
    }
}
