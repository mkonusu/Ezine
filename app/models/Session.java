package models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pkasturi on 11/1/14.
 * Session will store, the user email and the session token
 */
public class Session  implements Serializable {

    //Session token - UUID
    public String _id;
    public Date createdDate;
    public String email; //This is unique to the user
    public boolean isActive;

}
