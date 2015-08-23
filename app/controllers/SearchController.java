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


        }catch(Exception e) {
            e.printStackTrace();
            // response  - set empty or error code
        }

        return ok(new Gson().toJson(response));
    }
}
