package com.warmer.meta.dto;
import lombok.Data;

import java.util.List;

@Data
public class DataTableSubmitItem {

    private Integer dataSourceId;

    private List<String> dataTables;

    public Integer getDataSourceId() {
        return dataSourceId==null?0:dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId==null?0:dataSourceId;
    }
}
