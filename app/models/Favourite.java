package models;

import java.io.Serializable;

/**
 * @Author Murali
 */
public class Favourite implements Serializable {

    public enum ResourceType {
        CHANNEL, VIDEO, STATE, CATEGORY
    }

    public String _id;
    public String userId;
    public String resourceId;
    public String resourceType;
    public boolean isActive;


    public Favourite(String userId, String resourceId, String resourceType) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }
}
