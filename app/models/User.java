package models;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Murali Konusu
 */
public class User implements Serializable {

    public String _id;
    public String userName;
    public String email;
    public String password;
    public boolean isVerified;
    public Date timestamp;
    public boolean isActive;

    public User() {

    }

}
