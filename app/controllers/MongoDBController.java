package controllers;

import com.mongodb.*;
import com.typesafe.config.ConfigFactory;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import play.mvc.Controller;

import java.net.UnknownHostException;

/**
 * Mongodb controller for centralized mongo db operation
 * Created by muralik on 08/19/15.
 */
public class MongoDBController extends Controller{

    private static MongoClient client = null;
    private static DB database;
    public static Jongo jongo;

    public static void checkInit(){
        if (client == null)
            initMongoConnection();
    }

    public static void initMongoConnection (){

        try {
            client = new MongoClient(new MongoClientURI(ConfigFactory.load().getString("mongodb.uri")));
            client.setWriteConcern(WriteConcern.JOURNALED);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String db = ConfigFactory.load().getString("mongodb.db");
        database = client.getDB(db);
        jongo = new Jongo(client.getDB(db));

    }

    public static MongoCollection getCollection (String name){
        if (client == null){
            initMongoConnection();
        }
        return jongo.getCollection(name);
    }

    public static DB getDB(){
        return database;
    }
}
