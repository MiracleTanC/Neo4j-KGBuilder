package com.warmer.meta.dto;

import com.warmer.meta.entity.MetaDataSource;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Locale;

@Data
public class DatasourceSubmitItem {

    public Integer getDataSourceId() {
        return dataSourceId==null?0:dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId==null?0:dataSourceId;
    }

    private Integer dataSourceId;
    /**
     * 数据源类型
     */
    private String dbType;

    private String driverName;
    /**
     * IP及端口号
     */
    private String ipAndPort;
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


    public String buildConnUrl() {
        String dbType = this.getDbType().toUpperCase();
        String dbCode = this.getDbCode();
        String connUrl="";
        switch (dbType) {
            case "MYSQL":
                connUrl = String.format("jdbc:mysql://%s/%s?useSSL=false&serverTimezone=Asia/Shanghai", ipAndPort, dbCode);
                break;
            case "SQLSERVER":
                connUrl = String.format("jdbc:sqlserver://%s;databaseName=%s", ipAndPort, dbCode);
                break;
            case "POSTGRESQL":
                connUrl = String.format("jdbc:postgresql://%s", ipAndPort);
                break;
            case "ORACLE":
                connUrl = String.format("jdbc:oracle:thin:@//%s/%s", ipAndPort, dbCode);
                break;
            case "MARIADB":
                connUrl = String.format("jdbc:mariadb://%s/%s", ipAndPort, dbCode);
                break;
            case "HIVE":
                connUrl = String.format("jdbc:hive2://%s/%s", ipAndPort, dbCode);
                break;
            default:
                return "";
        }
        return connUrl;
    }

    public  MetaDataSource transfer(){
        MetaDataSource source = new MetaDataSource();
        BeanUtils.copyProperties(this, source);
        source.setConnectUrl(this.buildConnUrl());
        source.setIPAndPort(this.ipAndPort);
        return source;
    }
}
