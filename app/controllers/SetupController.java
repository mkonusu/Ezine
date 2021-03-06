package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mongodb.BasicDBObject;
import models.Language;
import models.User;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import util.CollectionNames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.jongo.Oid.withOid;

/**
 * @Author Murali
 */
public class SetupController extends Controller {

    public static Result languages() {

        MongoCollection languages = MongoDBController.getCollection(CollectionNames.languages);

        List<Language> defaultLanguages = new ArrayList<>();
        try (MongoCursor<Language> cursor = languages.find("{isActive : #}", true).as(Language.class)){
            while (cursor.hasNext()){
                Language lang = cursor.next();
                defaultLanguages.add(lang);
            }
        }catch (IOException e){
            Logger.error("Error processing jobs : " + e.getMessage());
        }
        JsonNode resp= new ObjectMapper().valueToTree(defaultLanguages);
        return ok(resp);
    }

    public static Result subscribeLanguage(String userId, String languageCode) {

        MongoCollection languages = MongoDBController.getCollection(CollectionNames.languages);
        MongoCollection users = MongoDBController.getCollection(CollectionNames.users);

        User fromDB = UserController.getUserFromSessionToken(userId);
        Language language = languages.findOne("{code : #}", languageCode).as(Language.class);


        if(fromDB == null) {

        } else {
            if(fromDB.languages == null) {
                fromDB.languages = new ArrayList<>();
            }

            boolean found = false;
            if(fromDB.languages.size() >0 ){
                // check if already exists

                for(Language lang: fromDB.languages) {
                    if(lang.code.equals(languageCode)) {
                        found = true;
                        break;
                    }
                }

            }
            if(!found) {
                fromDB.languages.add(language);
                users.save(fromDB);
            }
        }

        return ok("success");
    }

    public static Result unsubscribeLanguage(String userId, String languageCode) {

        MongoCollection users = MongoDBController.getCollection(CollectionNames.users);
        User user = UserController.getUserFromSessionToken(userId);
        if(user != null) {
            users.update("{_id: # }", user._id).upsert().multi().with("{ $pull : {languages : {'_id' : #}}}", languageCode);
        }

        return ok("success");
    }
}
