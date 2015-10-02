

import play.*;
import play.GlobalSettings;
import play.Logger;
import com.mongodb.DB;
import controllers.MongoDBController;
import org.jongo.MongoCollection;

import util.AppUtil;
import util.CollectionNames;

/**
 * Created by Murali on 02-10-2015.
 */
public class Global  extends GlobalSettings {


    public void onStart(Application app){

        MongoDBController.initMongoConnection();

        //create initial seed data
        DB db = MongoDBController.getDB();
        MongoCollection languages = MongoDBController.getCollection(CollectionNames.languages);
        MongoCollection categories = MongoDBController.getCollection(CollectionNames.categories);
        if (languages.count() <= 0){
            AppUtil.createLanguagesFromFile();
        }
        if (categories.count() <= 0){
            AppUtil.createCategoriesFromFile();
        }

        Logger.info ("Initialization of the application done .... Starting");
    }
}
