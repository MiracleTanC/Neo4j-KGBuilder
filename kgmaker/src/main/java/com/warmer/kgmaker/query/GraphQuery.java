package com.warmer.kgmaker.query;

public class GraphQuery{	

	private int domainid;
	private String domain;
	private String nodename;
	private String[] relation;
	private int matchtype ;
    private int pageSize = 10;
    private int pageIndex = 1;
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return the pageIndex
	 */
	public int getPageIndex() {
		return pageIndex;
	}
	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}
	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}
	/**
	 * @return the matchtype
	 */
	public int getMatchtype() {
		return matchtype;
	}
	/**
	 * @param matchtype the matchtype to set
	 */
	public void setMatchtype(int matchtype) {
		this.matchtype = matchtype;
	}
	/**
	 * @return the nodename
	 */
	public String getNodename() {
		return nodename;
	}
	/**
	 * @param nodename the nodename to set
	 */
	public void setNodename(String nodename) {
		this.nodename = nodename;
	}
	/**
	 * @return the relation
	 */
	public String[] getRelation() {
		return relation;
	}
	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String[] relation) {
		this.relation = relation;
	}
	/**
	 * @return the domainid
	 */
	public int getDomainid() {
		return domainid;
	}
	/**
	 * @param domainid the domainid to set
	 */
	public void setDomainid(int domainid) {
		this.domainid = domainid;
	}
	

}
