package com.warmer.base.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class GraphPageRecord<T> implements Serializable{	
    private int pageSize = 10;
    private int pageIndex = 1;
    private long totalCount = 0;
    private List<T> nodeList = new ArrayList<T>();


    /**
     * 获取分页记录数量
     */
    public int getPageSize() {
        return pageSize;
    }
    /**
     * 获取当前页序号
     */
    public int getPageIndex() {
        return pageIndex;
    }
    /**
     * 设置当前页序号
     */
    public void setPageIndex(int pageIndex) {
        if(pageIndex <= 0) {
            pageIndex = 1;
        }
        this.pageIndex = pageIndex;
    }
    public void setPageSize(int pageSize) {
        if(pageSize <= 0) {
        	pageSize = 1;
        }
        this.pageSize = pageSize;
    }
   
   
    /**
     * 获取总记录数    
     */
    public long getTotalCount() {
        return totalCount;
    }
    /**
     * 获取总记录数
     */
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
   
    /**
     * 获取Node检索结果列表
     */
    public List<T> getNodeList() {
        return nodeList;
    }
    public void setNodeList(List<T> nodeList) {
       this.nodeList=nodeList;
    }
}
