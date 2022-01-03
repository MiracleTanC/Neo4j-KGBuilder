package com.warmer.web.entity;

import lombok.Data;

import java.util.Date;

@Data
public class KgFeedBack {

    private Integer id;

    private String name;

    private String desc;

    private String type;

    private String email;

    private Date createTime;
}
