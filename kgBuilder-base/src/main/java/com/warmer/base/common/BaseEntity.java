package com.warmer.base.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class BaseEntity {
    public BaseEntity() {
        createUser = "tan";
        updateUser = createUser;
        createTime = new Date();
        updateTime = createTime;
        status = 1;
    }

    /**
     * 记录状态
     */
    private Integer status;
    /**
     * 创建用户
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改用户
     */
    private String updateUser;
    /**
     * 修改时间
     */
    private Date updateTime;


    @JsonIgnore
    public Integer getStatus() {
        return status;
    }

    @JsonProperty
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonIgnore
    public String getCreateUser() {
        return createUser;
    }

    @JsonProperty
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    @JsonIgnore
    public Date getCreateTime() {
        return createTime;
    }

    @JsonProperty
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonIgnore
    public String getUpdateUser() {
        return updateUser;
    }

    @JsonProperty
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @JsonIgnore
    public Date getUpdateTime() {
        return updateTime;
    }

    @JsonProperty
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
