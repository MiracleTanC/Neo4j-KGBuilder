package com.warmer.web.request;

import lombok.Data;

@Data
public class NodeCoordinateItem {
    private String domain;
    private String uuid;
    private Double fx;
    private Double fy;
}
