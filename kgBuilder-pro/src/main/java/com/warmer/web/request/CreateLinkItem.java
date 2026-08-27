package com.warmer.web.request;

import lombok.Data;

@Data
public class CreateLinkItem {
    private String domain;
    private String sourceId;
    private String targetId;
    private String ship;
}
