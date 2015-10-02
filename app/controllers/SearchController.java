package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.gson.Gson;
import com.typesafe.config.ConfigFactory;
import models.SearchRequest;
import models.SearchResponse;
import models.User;
import org.apache.commons.lang3.StringUtils;
import org.jongo.MongoCollection;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import util.CollectionNames;
import youtube.YSearch;
import youtube.util.CredentialRequiredException;
import youtube.util.ResponseMapper;

import java.io.File;


public class SearchController extends Controller {


    @BodyParser.Of(BodyParser.Json.class)
    public static Result search() {

        SearchResponse response = null;
        String SUPERUSER = request().getQueryString("SUPERUSER");
        try {

            SearchRequest searchRequest = null;
            if (request().body() != null || request().body().asJson() != null) {

                JsonNode json = request().body().asJson();
                searchRequest = new Gson().fromJson(json.toString(), models.SearchRequest.class);
            } else {
                // throw error message
            }
            String userId = null;
            if(searchRequest.userToken != null) {
                MongoCollection users = MongoDBController.getCollection(CollectionNames.users);
                User user = users.findOne("{ _id:# }", searchRequest.userToken).as(User.class);
                if(user != null ) userId = user._id;
            }
            SearchListResponse result = search(searchRequest);
            response = ResponseMapper.getSearchResponse(result, userId);

        }  catch(CredentialRequiredException e) {
            e.printStackTrace();
            if(StringUtils.trimToNull(SUPERUSER) !=  null) {
                String redirectUri = ConfigFactory.load().getString("youtube.api.authorize.uri");
                return redirect(redirectUri);
            } else {
                return ok("Network error !");
            }

            // response  - set empty or error code
        }
        catch(Exception e) {
            e.printStackTrace();
            // response  - set empty or error code
        }

        return ok(new Gson().toJson(response));
    }

    public static SearchListResponse  search(SearchRequest searchRequest) throws  CredentialRequiredException {
        return YSearch.videos(searchRequest);
    }
}
