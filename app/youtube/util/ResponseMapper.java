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


            System.out.println("getNextPageToken "+subscriptionListResponse.getNextPageToken());

            System.out.println("getPrevPageToken "+subscriptionListResponse.getPrevPageToken());

            response.kind = subscriptionListResponse.getKind();

            System.out.println("getVisitorId "+subscriptionListResponse.getVisitorId());
            System.out.println("getPageInfo "+subscriptionListResponse.getPageInfo());
            System.out.println("getTokenPagination "+subscriptionListResponse.getTokenPagination());
            if(subscriptionListResponse.getPageInfo() !=null) {
                response.recordsPerPage = subscriptionListResponse.getPageInfo().getResultsPerPage();
                response.totalRecords = subscriptionListResponse.getPageInfo().getTotalResults();

            }


            if(subscriptionListResponse.getItems() != null) {
                List<ChannelDetails> details =  new ArrayList<>(subscriptionListResponse.size());
                for (Subscription sub : subscriptionListResponse.getItems()) {
                    System.out.println("////// New Resource ///////");
                    System.out.println("getSnippet ID " + sub.getSnippet().getResourceId());
                    if(sub.getSnippet().getResourceId() !=null) {
                        System.out.println("getSnippet getResourceId getChannelId " + sub.getSnippet().getResourceId().getChannelId());
                        System.out.println("getSnippet getResourceId get " + sub.getSnippet().getResourceId().get("channelId"));
                        System.out.println("getSnippet getResourceId getChannelId " + sub.getSnippet().getResourceId().getChannelId());
                    }

                    System.out.println(" getSnippet Title " + sub.getSnippet().getTitle());
                    System.out.println(" getSnippet getChannelId " + sub.getSnippet().getChannelId());
                    System.out.println(" getSnippet get " + sub.getSnippet().get("channelId"));
                    System.out.println(" getSnippet getChannelTitle " + sub.getSnippet().getChannelTitle());
                    System.out.println(" getSnippet getDescription " + sub.getSnippet().getDescription());

                    System.out.println(" Resource getPublishedAt " + sub.getSnippet().getPublishedAt() );
                    System.out.println("Resource getEtag " + sub.getEtag());
                    System.out.println(" Resource getId " + sub.getId());
                    System.out.println(" Resource getKind " + sub.getKind());


                    System.out.println(" Resource getHigh " + sub.getSnippet().getThumbnails().getHigh().getUrl() +" "+sub.getSnippet().getThumbnails().getHigh().getHeight()+" "+sub.getSnippet().getThumbnails().getHigh().getWidth());
                    System.out.println(" Resource getMedium " + sub.getSnippet().getThumbnails().getMedium().getUrl()  +" "+sub.getSnippet().getThumbnails().getMedium().getHeight()+" "+sub.getSnippet().getThumbnails().getMedium().getWidth());
                    System.out.println(" Resource getMaxres " + sub.getSnippet().getThumbnails().getMaxres().getUrl()  +" "+sub.getSnippet().getThumbnails().getMaxres().getHeight()+" "+sub.getSnippet().getThumbnails().getMaxres().getWidth());
                    System.out.println(" Resource getStandard " + sub.getSnippet().getThumbnails().getStandard().getUrl()  +" "+sub.getSnippet().getThumbnails().getStandard().getHeight()+" "+sub.getSnippet().getThumbnails().getStandard().getWidth());
                    System.out.println(" Resource getDefault " + sub.getSnippet().getThumbnails().getDefault().getUrl()  +" "+sub.getSnippet().getThumbnails().getDefault().getHeight()+" "+sub.getSnippet().getThumbnails().getDefault().getWidth());

                    if(sub.getContentDetails() != null) {
                        SubscriptionContentDetails contentDetails = sub.getContentDetails();
                        System.out.println(" contentDetails getNewItemCount " + contentDetails.getNewItemCount());
                        System.out.println(" contentDetails getTotalItemCount " + contentDetails.getTotalItemCount());
                        System.out.println(" contentDetails getActivityType " + contentDetails.getActivityType());
                        System.out.println(" contentDetails values " + contentDetails.values());

                    }

                    if(sub.getSubscriberSnippet() != null) {
                        System.out.println(" getSubscriberSnippet getChannelId " + sub.getSubscriberSnippet().getChannelId());
                        System.out.println(" getSubscriberSnippet getDescription " + sub.getSubscriberSnippet().getDescription());
                        System.out.println(" getSubscriberSnippet getTitle " + sub.getSubscriberSnippet().getTitle());
                    }


                    ChannelDetails channelInfo =  new ChannelDetails();
                    channelInfo.channelId = sub.getSnippet().getResourceId().getChannelId();
                    channelInfo.description = sub.getSnippet().getDescription();
                    channelInfo.title = sub.getSnippet().getTitle();
                    details.add(channelInfo);





                    System.out.println();
                }
                response.subscribedChannels = details;
            }
        }


        return response;
    }
}
