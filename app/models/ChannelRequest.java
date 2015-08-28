package models;

/**
 * @Author Murali Konusu
 */
public class ChannelRequest {

    public String channelKey;
    public long recordsPerPage = 10;
    public String pageToken;

    public ChannelRequest() {

    }

    public ChannelRequest(long recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    public ChannelRequest(long recordsPerPage, String pageToken) {
        this.recordsPerPage = recordsPerPage;
        this.pageToken = pageToken;
    }
}
