package com.warmer.base.common;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author tc
 * 组合列之间过滤条件
 */
@Data
@ToString
public class FieldQueryItem implements Serializable {

    /**
     * 多个条件拼接 符号，or | and
     */
    private String joinOperate;
    /**
     * 字段和值之间的关系，0 模糊匹配% 还是 1 精确匹配=, -1=值类型，比较
     */
    private Integer condition;
    /**
     * 操作符 > | >=| = | < |<=
     */
    private String operate;
    /**
     * 数据库字段
     */
    private String field;
    /**
     * 检索文本
     */
    private String value;

}
