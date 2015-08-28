package models;

import java.util.List;

/**
 * @Author Murali Konusu
 */
public class ChannelResponse {

    public String kind;
    public String nextPageToken;
    public String prevPageToken;

    public long recordsPerPage;
    public long totalRecords;

    public List<ChannelDetails> subscribedChannels;

}
