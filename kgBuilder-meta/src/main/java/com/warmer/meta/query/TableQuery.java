package com.warmer.meta.query;

import com.warmer.base.common.FieldQueryItem;
import com.warmer.base.common.PageQuery;
import lombok.Data;

import java.util.List;

@Data
public class TableQuery extends PageQuery {
    private Integer dataSourceId;
    private Integer dataTableId;
    private String  dataTableName;
    private List<FieldQueryItem> filterItems;
}
