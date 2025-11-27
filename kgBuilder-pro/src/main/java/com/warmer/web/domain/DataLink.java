package com.warmer.web.domain;

import lombok.Data;

/**
 * 数据分析流程化配置中的 组件连接
 * ComponentLink
 */
@Data
public class DataLink {

	String sourceId;

	String targetId;

	String label;

}
