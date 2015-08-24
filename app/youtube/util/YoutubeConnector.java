package youtube.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Class to initialize youtube object with the api crentials
 *
 * @Author Murali Konusu
 */
public class YoutubeConnector {



    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    // This OAuth 2.0 access scope allows for full read/write access to the
    // authenticated user's account.
    private static List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

    public static YouTube getConnection () throws Exception{

        try {
            Credential credential = MyAuth.authorize(scopes, "videos");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(
                    "my-youtube-api-samples").build();

            return youtube;
        }catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
