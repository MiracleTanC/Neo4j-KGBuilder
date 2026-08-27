package com.warmer.web.model;

import lombok.Data;

@Data
public class NodeItem{
	/**
	 * 节点标识：图编辑器链路存 Neo4j elementId（形如 4:uuid:0，含冒号故必须为字符串）；
	 * 类别树管线（createNodeWithUUid/createLinkByUuid）存 SQL 分类节点 id 的字符串形态
	 */
	private String uuid;
	private String name;//显示名称
	private String domain;//显示名称
	private String color;//对应关系数据库字段
	private Integer r;
	/*private String x;
	private String y;*/
	public NodeItem(){

	}
	public NodeItem(String uuid,String name,String color){
		this.uuid=uuid;
		this.name=name;
		this.color=color;
	}
	public NodeItem(String uuid,String name,String color,Integer r){
		this.uuid=uuid;
		this.name=name;
		this.color=color;
		this.r=r;
	}
	public NodeItem(String uuid,String name,String color,Integer r,String domain){
		this.uuid=uuid;
		this.name=name;
		this.color=color;
		this.r=r;
		this.domain=domain;
	}
}
