/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import models.SearchRequest;
import youtube.util.CredentialRequiredException;
import youtube.util.YoutubeConnector;

import java.io.IOException;

/**
 * Class to return list of videos matching a search key.
 *
 * @author Murali Konusu
 */
public class Search {

    public static boolean GLOBAL_SEARCH = false;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * return the name and thumbnail image of each video in the result set.
     *
     * @param searchRequest search object.
     */
    public static SearchListResponse videos(SearchRequest searchRequest) throws CredentialRequiredException {

        try {
            // Define the API request for retrieving search results.
            YouTube.Search.List search = YoutubeConnector.getConnection().search().list("id,snippet");

            // Set your developer key from the {{ Google Cloud Console }} for
            // non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}

            search.setQ(searchRequest.searchKey);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("kind,nextPageToken,pageInfo,prevPageToken,tokenPagination,items(id/kind,id/videoId,snippet/title,snippet/channelId,snippet/thumbnails/default/url)");
            search.setMaxResults(searchRequest.recordsPerPage);

            // Call the API and print results.
            SearchListResponse searchListResponse = search.execute();

            return searchListResponse;
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
