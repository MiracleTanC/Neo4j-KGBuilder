package com.warmer.meta.entity;

import java.util.Date;
import java.io.Serializable;

import com.warmer.base.common.BaseEntity;
import lombok.Data;
/**
 * 数据源(MetaDataSource)实体类
 *
 * @author tanc
 * @since 2021-11-21 11:00:17
 */
@Data
public class MetaDataSource extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 931209553011500059L;
    /**
    * 数据源主键
    */
    private Integer dataSourceId;
    /**
    * 数据源类型
    */
    private String dbType;
    
    private String driverName;
    /**
    * IP及端口号
    */
    private String iPAndPort;
    /**
    * url
    */
    private String connectUrl;
    /**
    * 数据库别名
    */
    private String dbName;
    /**
    * 数据库名称
    */
    private String dbCode;
    /**
    * 用户名
    */
    private String dbUserName;
    /**
    * 密码
    */
    private String dbPassWord;
    /**
    * 最大连接数
    */
    private Integer maxPoolSize;
    /**
    * 数据库编码
    */
    private String databaseCoding;
    /**
    * 转换编码
    */
    private String transcoding;

}