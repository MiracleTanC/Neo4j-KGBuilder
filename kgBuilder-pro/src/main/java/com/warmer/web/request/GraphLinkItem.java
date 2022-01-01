package com.warmer.web.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GraphLinkItem implements Serializable {
    private String from;
    private String to;
    private String label;
}
