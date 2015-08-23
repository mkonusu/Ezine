package models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by home on 8/23/2015.
 */
public class SearchResponse implements Serializable {

    public String searchKey;

    public String nextPageToken;
    public String prevPageToken;

    public long recordsPerPage;
    public long totalRecords;

    public List<SearchVideo> videos;

}
