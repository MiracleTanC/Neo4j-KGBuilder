package com.warmer.web.request;


import com.warmer.base.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryNodeQuery extends PageQuery {
    private String Name;
}
