package com.warmer.kgmaker.entity;

import java.io.Serializable;
import java.util.List;

public class QAEntityItem  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long uuid;
	private String name;//显示名称
	private String field;//对应关系数据库字段
	private Integer entitytype;//0=概念实体,1=实体,2=属性,3=函数
	private Integer querytype;
	private Integer showstyle;//0=文本,1=地图,2=散点,3=柱图,4=饼图,5=折线图
	private String unit;//文本形式可能+单位
	private String sql;//查询函数
	private String table;//对应关系数据库表名
	private String detailshowfield;//细览页显示字段,在sql不为空时使用,其他不使用
	private String targettable;//对应关系数据库表名
	private Long sortcode;//排序码,越小越靠前
	private List<String> filter;//过滤条件<显示名字,数据库字段> 显示名字对应智能问答意图推测后台发布的字段

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public Long getSortcode() {
		return sortcode;
	}
	public void setSortcode(Long sortcode) {
		this.sortcode = sortcode;
	}
	public Integer getEntitytype() {
		return entitytype;
	}
	public void setEntitytype(Integer entitytype) {
		this.entitytype = entitytype;
	}

	public Integer getShowstyle() {
		return showstyle;
	}
	public void setShowstyle(Integer showstyle) {
		this.showstyle = showstyle;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<String> getFilter() {
		return filter;
	}
	public void setFilter(List<String> filter) {
		this.filter = filter;
	}
	/**
	 * @return the targettable
	 */
	public String getTargettable() {
		return targettable;
	}
	/**
	 * @param targettable the targettable to set
	 */
	public void setTargettable(String targettable) {
		this.targettable = targettable;
	}
	/**
	 * @return the querytype
	 */
	public Integer getQuerytype() {
		return querytype;
	}
	/**
	 * @param querytype the querytype to set
	 */
	public void setQuerytype(Integer querytype) {
		this.querytype = querytype;
	}
	/**
	 * @return the detailshowfield
	 */
	public String getDetailshowfield() {
		return detailshowfield;
	}
	/**
	 * @param detailshowfield the detailshowfield to set
	 */
	public void setDetailshowfield(String detailshowfield) {
		this.detailshowfield = detailshowfield;
	}
	/**
	 * @return the uuid
	 */
	public long getUuid() {
		return uuid;
	}
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(long uuid) {
		this.uuid = uuid;
	}
}
