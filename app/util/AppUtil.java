package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import controllers.MongoDBController;
import models.Category;
import models.Favourite;
import models.Language;
import org.jongo.MongoCollection;
import play.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Created by Murali on 02-10-2015.
 */
public class AppUtil {

    public static final String  FOLDER_PATH = "public/files/";

    private static final String LANGUAGE_JSON_FILE = "languages.json";
    private static final String CATEGORY_JSON_FILE = "youtubeVideoCategories.json";

    private static ObjectMapper om = new ObjectMapper();

    public static void createLanguagesFromFile() {

        MongoCollection languages = MongoDBController.getCollection(CollectionNames.languages);
        boolean opStatus = true;
        try {
            JsonNode doc = om.readTree(new File(FOLDER_PATH + LANGUAGE_JSON_FILE));
            JsonNode languageList = new ObjectMapper().readTree(doc.toString());
            for (JsonNode c1: languageList) {
                Language lang = new Gson().fromJson(c1.toString(), Language.class);
                languages.insert(lang);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Languages created from seed file : " + opStatus);
    }

    public static void createCategoriesFromFile() {

        MongoCollection categories = MongoDBController.getCollection(CollectionNames.categories);
        boolean opStatus = true;
        try {
            JsonNode doc = om.readTree(new File(FOLDER_PATH + CATEGORY_JSON_FILE));
            JsonNode categoriesList = new ObjectMapper().readTree(doc.toString());
            for (JsonNode c1: categoriesList) {
                Category category = new Gson().fromJson(c1.toString(), Category.class);
                categories.insert(category);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info("Categories created from seed file : " + opStatus);
    }

    public static boolean isFavourite(String userId, String resourceId, String resourceType) {

        boolean found = false;
        MongoCollection favourites = MongoDBController.getCollection(CollectionNames.favourites);
        Favourite fav = favourites.findOne("{userId:#, resourceId:#, resourceType:#}",userId, resourceId, resourceType).as(Favourite.class);
        if(fav !=  null) {

            if(fav.isActive) {
                found = true;
            }
        }

        return found;
    }
}
