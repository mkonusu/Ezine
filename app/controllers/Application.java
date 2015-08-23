package controllers;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import youtube.Search;

import java.util.List;

public class Application extends Controller {

    public static Result index() {

        SearchListResponse searchListResponse =  Search.videos(SearchController.getDefaultSearchRequest("Development"));
        List<SearchResult> searchResults = searchListResponse.getItems();
        for(SearchResult video : searchResults) {
            {
                ResourceId rId = video.getId();

                // Confirm that the result represents a video. Otherwise, the
                // item will not contain a video ID.
                if (rId.getKind().equals("youtube#video")) {
                    Thumbnail thumbnail = video.getSnippet().getThumbnails().getDefault();

                    System.out.println(video.getSnippet().getChannelId());
                    System.out.println(" Video Id" + rId.getVideoId());
                    System.out.println(" Title: " + video.getSnippet().getTitle());
                    System.out.println(" Thumbnail: " + thumbnail.getUrl());
                    System.out.println("\n-------------------------------------------------------------\n");
                }
            }
        }
        return ok(index.render("Your new application is ready."));
    }

}
