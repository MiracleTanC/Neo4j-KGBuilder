package com.warmer.web.request;


import com.warmer.base.common.PageQuery;
import lombok.Data;

@Data
public class CategoryNodeQuery extends PageQuery {
    private String Name;
}
