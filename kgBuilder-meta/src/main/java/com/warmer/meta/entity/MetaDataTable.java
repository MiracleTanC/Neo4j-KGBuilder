package com.warmer.meta.entity;

import java.util.Date;
import java.io.Serializable;

import com.warmer.base.common.BaseEntity;
import lombok.Data;
/**
 * (MetaDataTable)实体类
 *
 * @author tanc
 * @since 2021-11-21 11:00:20
 */
@Data
public class MetaDataTable extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 146313789945681038L;
    /**
    * 数据表主键
    */
    private Integer dataTableId;
    /**
    * 数据源id
    */
    private Integer datasourceId;
    /**
    * 表名
    */
    private String dataTableCode;
    /**
    * 表别名
    */
    private String dataTableName;


}