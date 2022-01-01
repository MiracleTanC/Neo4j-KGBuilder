package com.warmer.web.model;

import lombok.Data;

@Data
public class NodeItem{
	private long uuid;
	private String name;//显示名称
	private String domain;//显示名称
	private String color;//对应关系数据库字段
	private Integer r;
	/*private String x;
	private String y;*/
	public NodeItem(){

	}
	public NodeItem(long uuid,String name,String color){
		this.uuid=uuid;
		this.name=name;
		this.color=color;
	}
	public NodeItem(long uuid,String name,String color,Integer r){
		this.uuid=uuid;
		this.name=name;
		this.color=color;
		this.r=r;
	}
	public NodeItem(long uuid,String name,String color,Integer r,String domain){
		this.uuid=uuid;
		this.name=name;
		this.color=color;
		this.r=r;
		this.domain=domain;
	}
}
