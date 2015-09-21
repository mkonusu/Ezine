package models;

import java.io.Serializable;

/**
 * Created by home on 8/23/2015.
 */
public class SearchRequest implements Serializable {


    public String searchKey;
    public long recordsPerPage = 10;
    public String pageToken;
    public String order;
    public String channelId;


    public SearchRequest() {

    }

    public SearchRequest(String searchKey) {
        this.searchKey = searchKey;
    }

    public SearchRequest(String searchKey, long recordsPerPage) {
        this.searchKey = searchKey;
        this.recordsPerPage = recordsPerPage;
    }

}
