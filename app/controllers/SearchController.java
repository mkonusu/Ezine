package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.gson.Gson;
import com.typesafe.config.ConfigFactory;
import models.SearchRequest;
import models.SearchResponse;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import youtube.Search;
import youtube.util.CredentialRequiredException;
import youtube.util.ResponseMapper;

import java.io.File;


public class SearchController extends Controller {

    private static String DEFAULT_SEARCH_FILE = "public/files/default-search.json";
    private static final int DEFAULT_RECORDS_PER_PAGE = 10;

    public static Result search(String searchKey) {

        SearchResponse response = null;
        String SUPERUSER = request().getQueryString("SUPERUSER");
        try {
            ObjectMapper om = new ObjectMapper();
            SearchRequest searchRequest;
            if (request().body() == null || request().body().asJson() == null) {
                if(StringUtils.trimToNull(searchKey) !=null ) {
                    searchRequest = new SearchRequest(searchKey, DEFAULT_RECORDS_PER_PAGE);
                } else {
                    JsonNode searchReqNode = om.readTree(new File(DEFAULT_SEARCH_FILE));
                    searchRequest = om.treeToValue(searchReqNode, SearchRequest.class);
                }
            } else {
                JsonNode json = request().body().asJson();
                searchRequest = new Gson().fromJson(json.toString(), models.SearchRequest.class);
            }

            SearchListResponse result = search(searchRequest);
            response = ResponseMapper.getSearchResponse(result);

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
        return Search.videos(searchRequest);
    }
}
