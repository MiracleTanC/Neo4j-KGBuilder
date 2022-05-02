package com.warmer.web.entity;

import lombok.Data;

import java.util.Date;

@Data
public class KgDomain {
    private Integer id;
    private String name;
    private Integer nodeCount;
    private Integer shipCount;
    private Integer status;
    private String createUser;
    private Integer type;
    private Integer commend;
    private Date createTime;
    private Date modifyTime;
}
