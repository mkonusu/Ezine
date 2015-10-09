package models;

import org.joda.time.DateTime;


import java.io.Serializable;

/**
 * Created by Murali on 09-10-2015.
 */
public class UserActivity implements Serializable{

    public String _id;
    public String userId;
    public String activity; // Login,
    public String resourceId;
    public String resourceType;
    public DateTime activityOn; // performed on
}
