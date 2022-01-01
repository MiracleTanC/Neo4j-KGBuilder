package com.warmer.web.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class GraphNodeColumnItem implements Serializable {
    private String columnId;
    private String ico;
    private String isPrimary;
    private String itemId;
    private String itemCode;
    private String itemName;
    private String itemType;
    private Long nodeId;
}
