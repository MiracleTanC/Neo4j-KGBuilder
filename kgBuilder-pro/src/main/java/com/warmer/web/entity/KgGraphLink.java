package com.warmer.web.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * kg_graph_link
 * @author 
 */
@Data
public class KgGraphLink implements Serializable {
    private Long id;

    private String from;

    private String to;

    private String label;

    /**
     * 领域id
     */
    private Integer domainId;

    private static final long serialVersionUID = 1L;
}