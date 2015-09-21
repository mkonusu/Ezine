package models;

import java.io.Serializable;

/**
 * @Author Murali
 */
public class Pagination implements Serializable{

    public int recordsPerPage;
    public long totalRecords;
    public int pageNo;

    public Pagination() {

    }

    public Pagination(int recordsPerPage, int pageNo) {
        this.recordsPerPage = recordsPerPage;
        this.pageNo = pageNo;
    }


}
