package models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Murali on 28-08-2015.
 */
public class ChannelDetails implements Serializable {

    public String _id;
    public String channelId;
    public String channelTitle;
    public String channelUserName;
    public String description;
    public String language;

    public Date publishedAt;
    public String thumbnailUrl;

    public String categoryType;
    public String subscribedOn;
    public String subscribedBy;
    public boolean active;

    public ThumbnailInfo thumbnailInfo;


    public boolean isFavourite;

}
