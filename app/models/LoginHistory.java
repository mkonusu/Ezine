package models;

import java.io.Serializable;
import java.util.Date;


public class LoginHistory   implements Serializable {

    public String sessionToken;
    public boolean isRemember;
    public String email;
    public Date loginTime;
    public Date logoutTime;

}
