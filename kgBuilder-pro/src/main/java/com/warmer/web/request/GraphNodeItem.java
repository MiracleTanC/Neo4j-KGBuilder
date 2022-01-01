package com.warmer.web.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GraphNodeItem implements Serializable {
    private String nodeKey;
    private String nodeName;
    private String type;
    private String left;
    private String top;
    private String ico;
    private String state;
    private Integer viewOnly;
    private String alia;
    private Integer sourceId;
    private Integer tableId;
    private Integer domainId;
    private Integer startNode;
    private List<GraphNodeColumnItem> items;
}
