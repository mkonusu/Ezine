package youtube.util;

import com.google.api.services.youtube.model.*;
import models.ChannelDetails;
import models.ChannelResponse;
import models.SearchResponse;
import models.SearchVideo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by home on 8/23/2015.
 */
public class ResponseMapper {

    public static SearchResponse getSearchResponse(SearchListResponse searchListResponse) {

        SearchResponse response = new SearchResponse();

        response.nextPageToken =  searchListResponse.getNextPageToken();
        response.prevPageToken =   searchListResponse.getPrevPageToken();

        PageInfo pageInfo = searchListResponse.getPageInfo();
        if(pageInfo != null) {
            response.recordsPerPage = pageInfo.getResultsPerPage();
            response.totalRecords = pageInfo.getTotalResults();
        }

        List<SearchResult> searchResult = searchListResponse.getItems();
        if(searchResult !=null ) {
            List<SearchVideo> videos = new ArrayList<>(searchResult.size());

            for(SearchResult res: searchResult) {

                SearchVideo video = new SearchVideo();
                //ResourceId resource = res.getId();
                video.id = res.getId().getVideoId();
                video.kind = res.getKind();

                SearchResultSnippet snippet = res.getSnippet();
                video.channelId = snippet.getChannelId();
                video.channelTitle = snippet.getChannelTitle();
                video.description = snippet.getDescription();
                video.title = snippet.getTitle();

                Thumbnail defaultThumbnail = snippet.getThumbnails().getDefault();
                video.thumbnailUrl = defaultThumbnail.getUrl();

                videos.add(video);
            }

            response.videos = videos;
        }

        return response;
    }

    public static ChannelResponse getChannelResponse(SubscriptionListResponse subscriptionListResponse) {

        ChannelResponse response =  new ChannelResponse();
        //response.kind =  channelListResponse.getKind();
        //response.nextPageToken =  channelListResponse.getNextPageToken();
        //response.prevPageToken =  channelListResponse.getPrevPageToken();
        //response.recordsPerPage = channelListResponse.getPageInfo().getResultsPerPage();
        //response.totalRecords =  channelListResponse.getPageInfo().getTotalResults();

       ;



        if(subscriptionListResponse !=null ) {
            System.out.println("============ Response ==================");
            System.out.println("getEtag " + subscriptionListResponse.getEtag());
            System.out.println("getEventId "+subscriptionListResponse.getEventId());
            System.out.println("getKind "+subscriptionListResponse.getKind());
            response.kind = subscriptionListResponse.getKind();

            System.out.println("getNextPageToken "+subscriptionListResponse.getNextPageToken());
            response.kind = subscriptionListResponse.getNextPageToken();
            System.out.println("getPrevPageToken "+subscriptionListResponse.getPrevPageToken());
            response.kind = subscriptionListResponse.getPrevPageToken();

            System.out.println("getVisitorId "+subscriptionListResponse.getVisitorId());
            System.out.println("getPageInfo "+subscriptionListResponse.getPageInfo());
            System.out.println("getTokenPagination "+subscriptionListResponse.getTokenPagination());




            if(subscriptionListResponse.getItems() != null) {
                List<ChannelDetails> details =  new ArrayList<>(subscriptionListResponse.size());
                for (Subscription sub : subscriptionListResponse.getItems()) {
                    System.out.println("////// New Resource ///////");
                    System.out.println("Resource ID " + sub.getSnippet().getResourceId());
                    System.out.println(" Resource Title " + sub.getSnippet().getTitle());
                    System.out.println(" Resource getChannelId " + sub.getSnippet().getChannelId());
                    ChannelDetails channelInfo =  new ChannelDetails();
                    channelInfo.channelId = sub.getSnippet().getChannelId();
                    channelInfo.description = sub.getSnippet().getDescription();
                    channelInfo.title = sub.getSnippet().getChannelTitle();
                    details.add(channelInfo);


                    System.out.println("Resource getDescription " + sub.getSnippet().getDescription());
                    System.out.println(" Resource getChannelTitle " + sub.getSnippet().getChannelTitle());
                    System.out.println(" Resource getPublishedAt " + sub.getSnippet().getPublishedAt() );
                    System.out.println("Resource getEtag " + sub.getEtag());
                    System.out.println(" Resource getId " + sub.getId());
                    System.out.println(" Resource getKind " + sub.getKind());

                    SubscriptionContentDetails contentDetails = sub.getContentDetails();
                    System.out.println(" contentDetails getNewItemCount " + contentDetails.getNewItemCount());
                    System.out.println(" contentDetails getTotalItemCount " + contentDetails.getTotalItemCount());
                    System.out.println(" contentDetails getActivityType " + contentDetails.getActivityType());
                    System.out.println(" contentDetails values " + contentDetails.values());

                    System.out.println();
                }
            }
        }


        return response;
    }
}
