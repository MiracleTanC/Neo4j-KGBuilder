package com.warmer.base.common;

public class PageQuery {
    private int currentPage=1;
    private int pageSize=10;

    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        if(currentPage==0){
            currentPage=1;
        }
        this.currentPage = currentPage;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
