package com.warmer.web.entity;

import lombok.Data;

@Data
public class KgNodeDetail {
    private Integer id;
    private Integer domainId;
    private Integer nodeId;
    private String content;
    private Integer status;
    private String createUser;
    private String createTime;
    private String modifyUser;
    private String modifyTime;
}
