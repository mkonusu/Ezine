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

    public static ChannelResponse getChannelResponsee(ChannelListResponse channelListResponse) {

        ChannelResponse response =  new ChannelResponse();
        response.kind =  channelListResponse.getKind();
        response.nextPageToken =  channelListResponse.getNextPageToken();
        response.prevPageToken =  channelListResponse.getPrevPageToken();
        response.recordsPerPage = channelListResponse.getPageInfo().getResultsPerPage();
        response.totalRecords =  channelListResponse.getPageInfo().getTotalResults();

        List<com.google.api.services.youtube.model.Channel> channels = channelListResponse.getItems();

        if(channels!=null && channels.size() >0) {
            List<ChannelDetails> channelDetails =  new ArrayList<>();
            for(com.google.api.services.youtube.model.Channel channel : channels) {
                ChannelDetails channelInfo =  new ChannelDetails();

                channelInfo.type = channel.getKind();
                channelInfo.channelId = channel.getId();
                channelInfo.description = channel.getSnippet().getDescription();
                //channelInfo.publichedAt = channel.getSnippet().getPublishedAt().;

                Thumbnail defaultThumbnail = channel.getSnippet().getThumbnails().getDefault();
                channelInfo.thumbnailUrl = defaultThumbnail.getUrl();

                channelInfo.title = channel.getSnippet().getTitle();

                channelDetails.add(channelInfo);
            }
        }

        return response;
    }
}
