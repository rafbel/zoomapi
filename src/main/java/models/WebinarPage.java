package models;

import java.util.List;

public class WebinarPage {

    private int pageCount;
    private int pageNumber;
    private int pageSize;
    private int totalRecords;
    private List<WebinarFile> webinars;

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public List<WebinarFile> getWebinars() {
        return webinars;
    }

    public void setWebinars(List<WebinarFile> webinars) {
        this.webinars = webinars;
    }
}
