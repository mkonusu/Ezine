package youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import models.ChannelDetails;
import models.ChannelRequest;
import models.ChannelResponse;
import youtube.util.CredentialRequiredException;
import youtube.util.ResponseMapper;
import youtube.util.YoutubeConnector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Murali Konusu
 */
public class YChannel {

    public static ChannelListResponse list(ChannelRequest channelRequest) throws CredentialRequiredException{
        try {
            // Define the API request for retrieving search results.
            YouTube.Channels.List channelsList  = YoutubeConnector.getConnection().channels().list("id,snippet");
            channelsList.setMine(true);
            channelsList.setMaxResults(channelRequest.recordsPerPage);
            channelsList.setFields("kind,nextPageToken,pageInfo,prevPageToken,tokenPagination,items(id/kind,id/videoId,snippet/title,snippet/channelId,snippet/thumbnails/default/url)");

            ChannelListResponse channelListResponse = channelsList.execute();

            return channelListResponse;
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        }   catch (CredentialRequiredException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            throw e;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;

    }

}
