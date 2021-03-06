package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import models.Language;
import models.LoginHistory;
import models.Session;
import models.User;
import org.joda.time.DateTime;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import play.Logger;
import play.i18n.Messages;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.CollectionNames;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User management activities
 *
 * @Author Murali Konusu
 */
public class UserController  extends Controller {


    public static final String USER_COOKIE = "USER_COOKIE";
    public static final int DEFAULT_COOKIE_DURATION = 20*24*3600; //Seconds for cookie


    /**
     * Verify user emailId and password in the registered records
     *
     * @return Result - status of login verification (SUCCESS - User details / FAILURE - INVALID DETAILS)
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result verify() {

        //Get from form submission
        JsonNode json = request().body().asJson();
        User user = new Gson().fromJson(json.toString(), User.class);

        User fromDB = authenticate(user);
        if (fromDB == null ) {

            ObjectNode resp = new ObjectMapper().createObjectNode();
            resp.put("msg", "Account doesn't exists!");
            //return unauthorized(resp);
            return ok(resp);
        }

        Session session = createUserSession(fromDB);
        createLoginHistory(fromDB);

        fromDB.password = ""; // remove password in response

        ObjectMapper om = new ObjectMapper();
        ObjectNode res = om.createObjectNode();
        res.put("userToken", session._id);
        res.put("userDetails", new Gson().toJson(fromDB));

        return ok(res);
    }

    public static User authenticate(User user) {

        String passwd = getEncodedPassword(user.password);
        MongoCollection users = MongoDBController.getCollection(CollectionNames.users);

        User fromDB = users.findOne("{email : #, password : #}", user.email, passwd).as(User.class);
        if(fromDB != null) {
            fromDB.password = "";
        }
        return fromDB;
    }

    public static Result getUser(String userId) {

        User user = getUserFromSessionToken(userId);


        return ok(new Gson().toJson(user));
    }


    /**
     * Register a new user
     *
     * @return Result - status of registration (SUCCESS / FAILURE - ALREADY EXISTS, INSUFFIECIENT DATA, INVALID DATA)
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result register() {

        JsonNode json = request().body().asJson();
        User user = new Gson().fromJson(json.toString(), User.class);
        MongoCollection users = MongoDBController.getCollection(CollectionNames.users);

        // check user already exists

        User fromDB = users.findOne("{email : #}", user.email).as(User.class);
        Session session  = null;
        if(fromDB ==  null) {
            String password = getEncodedPassword(user.password);
            user._id = UUID.randomUUID().toString();
            user.password = password;
            user.languages = new ArrayList<>();
            users.save(user);
            session  = createUserSession(user);
            createLoginHistory(user);
        } else {
            return ok("User already exists");
        }
        fromDB = users.findOne("{email : #}", user.email).as(User.class);
        fromDB.password = null;

        ObjectMapper om = new ObjectMapper();
        ObjectNode res = om.createObjectNode();
        if(session != null) {
            res.put("userToken", session._id);
        }
        res.put("userDetails", new Gson().toJson(fromDB));


        return ok(new Gson().toJson(fromDB));
    }


    /**
     * Store in login history collection
     * @param foundUser
     */
    private static void createLoginHistory(User foundUser) {

        MongoCollection history = MongoDBController.getCollection(CollectionNames.loginHistory);
        MongoCollection sessions = MongoDBController.getCollection(CollectionNames.session);
        Session session = sessions.findOne("{email: #, isActive : true}", foundUser.email).as(Session.class);

        LoginHistory hist = new LoginHistory();
        hist.email = foundUser.email;

        hist.sessionToken  = session._id;
        hist.loginTime = new DateTime().toDate();
        history.save(hist);
        Logger.info("Login history created for :" + foundUser.email);
    }

    /**
     * Method for encrypting a password ... Password once encrypted cannot be retrieved
     * @param pwd
     * @return
     */
    public static String getEncodedPassword (String pwd){

        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] result = mDigest.digest(pwd.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }


    private static Session createUserSession(User user) {

        Session session = new Session();
        session._id = UUID.randomUUID().toString();
        session.createdDate = new DateTime().toDate();
        session.email = user.email;
        session.isActive = true;


        MongoCollection sessions = MongoDBController.getCollection(CollectionNames.session);
        sessions.update("{email : #}", user.email).multi().with("{$set: {isActive : false}}");
        sessions.save(session);
        setCookieForUser(session, DEFAULT_COOKIE_DURATION);
        return session;
    }

    /**
     *  Cookie will be set for 7 days or till logout which ever is earlier
    * @param session
    */
    private static void setCookieForUser(Session session, int duration){

        try {
            Integer maxAge = new Integer(duration);
            response().setCookie(USER_COOKIE, session._id, maxAge);
        } catch (Exception e){
            Logger.info("Masking for unit tests");
        }

    }


    /**
     * Getting the user from the cookies
     * @return
     */
    public static User getUserFromCookie() {
        User user = null;
        try{
            String sessionToken = getCookieValue(USER_COOKIE);
            if (sessionToken != null){
                MongoCollection sessions = MongoDBController.getCollection(CollectionNames.session);
                Session active = sessions.findOne("{_id : #}", sessionToken).as(Session.class);
                if (active != null){
                    MongoCollection users = MongoDBController.getCollection(CollectionNames.users);
                    user = users.findOne("{email : #}", active.email).as(User.class);
                }
            }
        }catch (NullPointerException npe){
            Logger.info("User information not in the cookie ...");
        }
        return user;
    }



    public static User getUserFromSessionToken(String userToken) {
        User user = null;
        try{
            // sessionToken = getCookieValue(USER_COOKIE);
            if (userToken != null) {
                MongoCollection sessions = MongoDBController.getCollection(CollectionNames.session);
                Session active = sessions.findOne("{_id : #}", userToken).as(Session.class);
                if (active != null){
                    MongoCollection users = MongoDBController.getCollection(CollectionNames.users);
                    user = users.findOne("{email : #}", active.email).as(User.class);
                }
            }
        }catch (NullPointerException npe){
            Logger.info("User information not in the cookie ...");
        }
        return user;
    }

    /**
     * Retrieve cookie value
     * @param cookieName
     * @return
     */
    public static String getCookieValue (String cookieName){
        Http.Cookie cookie = request().cookie(cookieName);
        if(cookie != null) {
            return cookie.value();
        }
        return null;
    }

    public static Result subscribedLanguages(String userId) {

        MongoCollection users = MongoDBController.getCollection(CollectionNames.users);

        User fromDB = getUserFromSessionToken(userId);

        if(fromDB != null) {
            JsonNode resp= new ObjectMapper().valueToTree(fromDB.languages);
            return ok(resp);
        } else {
            // send invalid user
            return ok("Invalid User!");
        }
    }


    /**
     * Send reset paswword instructions email to user's emailId
     * @param emailId
     *
     * @return Result -  (SUCCESS / FAILURE - emailId doesnot exists)
     */
    public static Result forgotPassword(String emailId) {
        return ok("Success !");
    }

    /**
     * Reset user's password
     * @return Result
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result resetPassword() {
        return ok("Success !");
    }

    /**
     * Verify user registration token sent to emailId with DB
     * @param token
     * @return Result
     */
    public static Result validateToken(String token) {
        return ok("Success !");
    }

    /**
     * Verify user reset password token sent to emailId with DB
     * @param token
     * @return Result
     */
    public static Result validateResetPasswordToken(String token) {
        return ok("Success !");
    }


    /**
     * Save selected topics of the user
     *
     * @return Result
     */
    public static Result setTopics() {
        return ok("Success !");
    }
}
