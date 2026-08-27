package com.warmer.web.entity;

import lombok.Data;

@Data
public class KgNodeDetail {
    private Integer id;
    private Integer domainId;
    /**
     * 节点标识，Neo4j 5 起 elementId 形如 "4:uuid:0"，故用字符串存储
     */
    private String nodeId;
    private String content;
    private Integer status;
    private String createUser;
    private String createTime;
    private String modifyUser;
    private String modifyTime;
}
