package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.gson.Gson;
import models.SearchRequest;
import models.SearchResponse;
import play.mvc.Controller;
import play.mvc.Result;
import youtube.Search;
import youtube.util.CredentialRequiredException;
import youtube.util.ResponseMapper;

import java.io.File;


public class SearchController extends Controller {

    private static String DEFAULT_SEARCH_FILE = "public/files/default-search.json";

    public static Result search(String topic) {

        SearchResponse response = null;
        try {
            ObjectMapper om = new ObjectMapper();
            SearchRequest searchRequest;
            if (request().body() == null || request().body().asJson() == null) {
                JsonNode searchReqNode = om.readTree(new File(DEFAULT_SEARCH_FILE));
                searchRequest = om.treeToValue(searchReqNode, SearchRequest.class);
            } else {
                JsonNode json = request().body().asJson();
                searchRequest = new Gson().fromJson(json.toString(), models.SearchRequest.class);
            }
            SearchListResponse result = Search.videos(searchRequest);
            response = ResponseMapper.getSearchResponse(result);


        }catch(CredentialRequiredException e) {
            e.printStackTrace();
            return redirect("https://accounts.google.com/o/oauth2/auth?client_id=506479486719-8sar4lu3vsoihmck1k5fie8blbe1q947.apps.googleusercontent.com&redirect_uri=http%3A%2F%2Flocalhost:9000%2FCallback&scope=https://www.googleapis.com/auth/youtube&response_type=code&access_type=offline");
            // response  - set empty or error code
        }
        catch(Exception e) {
            e.printStackTrace();
            // response  - set empty or error code
        }

        return ok(new Gson().toJson(response));
    }
}
