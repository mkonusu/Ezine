package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.gson.Gson;
import models.SearchRequest;
import models.SearchResponse;
import play.mvc.Controller;
import play.mvc.Result;
import youtube.Search;
import youtube.util.ResponseMapper;


public class SearchController extends Controller {

    public static Result search(String topic) {

        SearchRequest searchRequest = null;
        if( request().body() == null || request().body().asJson() == null) {
            searchRequest = getDefaultSearchRequest(topic);
        } else {
            JsonNode json = request().body().asJson();
            searchRequest = new Gson().fromJson(json.toString(), models.SearchRequest.class);
        }
        SearchListResponse result = Search.videos(searchRequest);
        SearchResponse response = ResponseMapper.getSearchResponse(result);

        return ok(new Gson().toJson(response));
    }


    public static SearchRequest getDefaultSearchRequest(String topic) {
        SearchRequest req = new SearchRequest();
        req.recordsPerPage =10;
        req.searchKey= topic;
        if(topic == null)  req.searchKey= "Technology";

        return req;
    }
}
