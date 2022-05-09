package com.warmer.web.request;

import lombok.Data;

import java.util.List;

@Data
public class NodeCoordinateSubmitItem {
    private String domain;
    private List<NodeCoordinateItem> nodes;
}
