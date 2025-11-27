package com.warmer.web.service;

import com.warmer.web.request.GraphItem;

import java.io.IOException;

/**
 * 图谱节点管理服务接口
 *
 * 提供图谱节点的创建与领域节点查询能力。
 */
public interface KgGraphNodeService {

    /**
     * 创建图谱节点
     * @param submitItem 节点提交项
     * @throws IOException IO 异常
     */
    void createNode(GraphItem submitItem) throws IOException;

    /**
     * 获取领域的节点数据
     * @param domainId 领域ID
     * @return 领域节点
     * @throws IOException IO 异常
     */
    GraphItem getDomainNode(Integer domainId) throws IOException;
}
