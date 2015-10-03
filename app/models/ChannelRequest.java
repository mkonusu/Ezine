package models;

/**
 * @Author Murali Konusu
 */
public class ChannelRequest {

    public String userToken;
    public Pagination pagination;
    public String language;
    public String categoryId;

    public ChannelRequest() {

    }

    public ChannelRequest(int recordsPerPage, int pageNo) {
       this.pagination = new Pagination(recordsPerPage, pageNo);
    }

    public ChannelRequest(int recordsPerPage, int pageNo, String language) {
        this.pagination = new Pagination(recordsPerPage, pageNo);
        this.language = language;
    }
}
