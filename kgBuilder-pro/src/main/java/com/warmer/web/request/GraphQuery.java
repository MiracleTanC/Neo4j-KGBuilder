package com.warmer.web.request;

import lombok.Data;

@Data
public class GraphQuery{	

	private int domainId;
	private Integer type;//0=手动创建，1=三元组导入，2=excel导入，3=er图构建
	private Integer commend;
	private String domain;
	private String nodeName;
	private String[] relation;
	private int matchType;
    private int pageSize = 10;
    private int pageIndex = 1;
}
