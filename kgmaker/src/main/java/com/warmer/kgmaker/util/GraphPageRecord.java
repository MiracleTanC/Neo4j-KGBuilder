package com.warmer.kgmaker.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class GraphPageRecord<T> implements Serializable{	
    private int pageSize = 10;
    private int pageIndex = 1;
    private int totalCount = 0;
    private List<T> nodeList = new ArrayList<T>();


    /**
     * 获取分页记录数量
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }
    /**
     * 获取当前页序号
     * @return
     */
    public int getPageIndex() {
        return pageIndex;
    }
    /**
     * 设置当前页序号
     * @param pageIndex
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
    public int getTotalCount() {
        return totalCount;
    }
    /**
     * 获取总记录数    
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
   
    /**
     * 获取Node检索结果列表
     * @return
     */
    public List<T> getNodeList() {
        return nodeList;
    }
    public void setNodeList(List<T> nodeList) {
       this.nodeList=nodeList;
    }
}
