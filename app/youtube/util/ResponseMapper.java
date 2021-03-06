package youtube.util;

import com.google.api.services.youtube.model.*;
import models.*;
import util.AppUtil;
import youtube.YSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by home on 8/23/2015.
 */
public class ResponseMapper {

    public static SearchResponse getSearchResponse(SearchListResponse searchListResponse, String userId) throws CredentialRequiredException {

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


                Video videoDetails = YSearch.video(video.id);
                if(videoDetails != null) {
                    video.thumbnailInfo = getThumbnails(videoDetails.getSnippet().getThumbnails());
                }
                if(userId !=null)  {
                    video.isFavourite = AppUtil.isFavourite(userId, video.id, "VIDEO");
                }

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
            System.out.println("getEtag " + subscriptionListResponse.getEtag()); // W
            System.out.println("getEventId "+subscriptionListResponse.getEventId()); // NW
            System.out.println("getKind "+subscriptionListResponse.getKind()); // W


            System.out.println("getNextPageToken "+subscriptionListResponse.getNextPageToken()); // W

            System.out.println("getPrevPageToken "+subscriptionListResponse.getPrevPageToken()); // W

            //response.kind = subscriptionListResponse.getKind();

            System.out.println("getVisitorId "+subscriptionListResponse.getVisitorId()); // NW
            System.out.println("getPageInfo "+subscriptionListResponse.getPageInfo()); // W
            System.out.println("getTokenPagination "+subscriptionListResponse.getTokenPagination()); // W

            if(subscriptionListResponse.getPageInfo() !=null) {

                 subscriptionListResponse.getPageInfo().getResultsPerPage(); // W
                 subscriptionListResponse.getPageInfo().getTotalResults(); // W

            }

            if(subscriptionListResponse.getItems() != null) {
                List<ChannelDetails> details =  new ArrayList<>(subscriptionListResponse.size());
                for (Subscription sub : subscriptionListResponse.getItems()) {
                    details.add(getChannelResponse(sub));
                }
                response.subscribedChannels = details;
            }
        }


        return response;
    }



    public static ChannelDetails getChannelResponse(Subscription sub) {


        if(sub !=null ) {

            ChannelDetails channelInfo =  new ChannelDetails();
            channelInfo.channelId = (String)sub.getSnippet().getResourceId().get("channelId");

            System.out.println(" channel id from response "+channelInfo.channelId);
            channelInfo.description = sub.getSnippet().getDescription();
            channelInfo.channelTitle = sub.getSnippet().getTitle();

            if(sub.getSnippet().getThumbnails() !=null && sub.getSnippet().getThumbnails().size()>0) {
                channelInfo.thumbnailInfo = getThumbnails(sub.getSnippet().getThumbnails());
            }

            return channelInfo;
        }


        return null;
    }


    public static ThumbnailInfo getThumbnails(ThumbnailDetails thumbnailDetails) {
        ThumbnailInfo thumbnailInfo = new ThumbnailInfo();
        Thumbnail tdHigh = thumbnailDetails.getHigh();
        Thumbnail tdMaxres = thumbnailDetails.getMaxres();
        Thumbnail tdMedium = thumbnailDetails.getMedium();
        Thumbnail tdStandard = thumbnailDetails.getStandard();
        Thumbnail tdDefault = thumbnailDetails.getDefault();

        if(tdHigh != null) {
            thumbnailInfo.highUrl = tdHigh.getUrl();
            if(tdHigh.getHeight() !=null && tdHigh.getHeight() >0 ) thumbnailInfo.highHeight = tdHigh.getHeight();
            if(tdHigh.getWidth() !=null && tdHigh.getWidth() >0 ) thumbnailInfo.highWidth = tdHigh.getWidth();
        }
        if(tdMaxres != null) {
            thumbnailInfo.maxresUrl = tdMaxres.getUrl();
            if(tdMaxres.getHeight() !=null && tdMaxres.getHeight() >0 ) thumbnailInfo.maxresHeight = tdMaxres.getHeight();
            if(tdMaxres.getWidth() !=null && tdMaxres.getWidth() >0 ) thumbnailInfo.maxresWidth = tdMaxres.getWidth();
        }
        if(tdMedium != null) {
            thumbnailInfo.mediumUrl = tdMedium.getUrl();
            if(tdMedium.getHeight() !=null && tdMedium.getHeight() >0 ) thumbnailInfo.mediumHeight = tdMedium.getHeight();
            if(tdMedium.getWidth() !=null && tdMedium.getWidth() >0 ) thumbnailInfo.mediumWidth = tdMedium.getWidth();
        }
        if(tdStandard != null) {
            thumbnailInfo.standardUrl = tdStandard.getUrl();
            if(tdStandard.getHeight() !=null && tdStandard.getHeight() >0 ) thumbnailInfo.standardHeight = tdStandard.getHeight();
            if(tdStandard.getWidth() !=null && tdStandard.getWidth() >0 ) thumbnailInfo.standardWidth = tdStandard.getWidth();
        }
        if(tdDefault != null) {
            thumbnailInfo.defaultUrl = tdDefault.getUrl();
            if(tdDefault.getHeight() !=null && tdDefault.getHeight() >0 ) thumbnailInfo.defaultHeight = tdDefault.getHeight();
            if(tdDefault.getWidth() !=null && tdDefault.getWidth() >0 ) thumbnailInfo.defaultWidth = tdDefault.getWidth();
        }

        return thumbnailInfo;
    }


    public static void prettyPrint(Subscription sub) {
        System.out.println("////// New Resource ///////");
        System.out.println("getSnippet ID " + sub.getSnippet().getResourceId()); //  {"channelId":"UCyElywpP4PArZI6Ws3rzAsw","kind":"youtube#channel"}
        if(sub.getSnippet().getResourceId() !=null) {
            System.out.println("getSnippet getResourceId getChannelId " + sub.getSnippet().getResourceId().getChannelId()); // W
            System.out.println("getSnippet getResourceId get " + sub.getSnippet().getResourceId().get("channelId")); // W
            System.out.println("getSnippet getResourceId getChannelId " + sub.getSnippet().getResourceId().getChannelId()); // W
        }

        System.out.println(" getSnippet Title " + sub.getSnippet().getTitle()); // WORKING - USE
        System.out.println(" getSnippet getChannelId " + sub.getSnippet().getChannelId()); // DONOT USE
        System.out.println(" getSnippet get " + sub.getSnippet().get("channelId")); // WORKING - USE
        System.out.println(" getSnippet getChannelTitle " + sub.getSnippet().getChannelTitle()); // NW
        System.out.println(" getSnippet getDescription " + sub.getSnippet().getDescription()); // USE

        System.out.println(" Resource getPublishedAt " + sub.getSnippet().getPublishedAt() ); // W
        System.out.println("Resource getEtag " + sub.getEtag()); // W
        System.out.println(" Resource getId " + sub.getId()); // NOT REQUIRED
        System.out.println(" Resource getKind " + sub.getKind()); // W


        System.out.println(" Resource getHigh " + sub.getSnippet().getThumbnails().getHigh().getUrl() +" "+sub.getSnippet().getThumbnails().getHigh().getHeight()+" "+sub.getSnippet().getThumbnails().getHigh().getWidth());
        if(sub.getSnippet().getThumbnails().getMedium() != null) {
            System.out.println(" Resource getMedium " + sub.getSnippet().getThumbnails().getMedium().getUrl() + " " + sub.getSnippet().getThumbnails().getMedium().getHeight() + " " + sub.getSnippet().getThumbnails().getMedium().getWidth());
        }
        if(sub.getSnippet().getThumbnails().getMaxres() != null) {
            System.out.println(" Resource getMaxres " + sub.getSnippet().getThumbnails().getMaxres().getUrl() + " " + sub.getSnippet().getThumbnails().getMaxres().getHeight() + " " + sub.getSnippet().getThumbnails().getMaxres().getWidth());
        }
        if(sub.getSnippet().getThumbnails().getStandard() != null) {
            System.out.println(" Resource getStandard " + sub.getSnippet().getThumbnails().getStandard().getUrl() + " " + sub.getSnippet().getThumbnails().getStandard().getHeight() + " " + sub.getSnippet().getThumbnails().getStandard().getWidth());
        }
        if(sub.getSnippet().getThumbnails().getDefault() != null) {
            System.out.println(" Resource getDefault " + sub.getSnippet().getThumbnails().getDefault().getUrl() + " " + sub.getSnippet().getThumbnails().getDefault().getHeight() + " " + sub.getSnippet().getThumbnails().getDefault().getWidth());
        }

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
    }


}
