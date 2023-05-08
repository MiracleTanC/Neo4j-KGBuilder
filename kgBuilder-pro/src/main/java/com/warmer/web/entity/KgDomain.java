package com.warmer.web.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Tolerate;

import java.util.Date;

@Data
@Builder
public class KgDomain {
    private Integer id;
    private String name;
    private String label;
    private long nodeCount;
    private long shipCount;
    private Integer status;
    private String createUser;
    private String modifyUser;
    private Integer type;
    private Integer commend;
    private Date createTime;
    private Date modifyTime;
    @Tolerate
    public KgDomain(){

    }
}
