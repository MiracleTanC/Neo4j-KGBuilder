package com.warmer.web.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GraphItem implements Serializable {
    Integer domainId;
    String domainName;
    private List<GraphNodeItem> nodeList;
    private List<GraphLinkItem> lineList;
}
