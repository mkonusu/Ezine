package models;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

    public List<Language> languages;

    public User() {
    }
}
