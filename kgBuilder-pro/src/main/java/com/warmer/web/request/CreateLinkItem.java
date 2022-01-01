package com.warmer.web.request;

import lombok.Data;

@Data
public class CreateLinkItem {
    private String domain;
    private long sourceId;
    private long targetId;
    private String ship;
}
