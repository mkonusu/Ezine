package youtube.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.common.collect.Lists;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.util.List;

/**
 * Class to initialize youtube object with the api credentials
 *
 * @Author Murali Konusu
 */
public class YoutubeConnector {


    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;


    private static YouTube youtubeForSearch;

    // This OAuth 2.0 access scope allows for full read/write access to the
    // authenticated user's account.
    private static List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

    /**
     * Youtube connection with OAuth 2.0
     *
     * @return YouTube
     * @throws Exception
     */
    public static YouTube getConnection () throws Exception{

        try {
            Credential credential = MyAuth.authorize(scopes, "videos");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(
                    ConfigFactory.load().getString("application.name")).build();

            return youtube;
        }catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Youtube connection with API Key
     *
     * @return YouTube
     * @throws Exception
     */
    public static YouTube getConnectionForSearch () throws Exception{

        try {
            youtubeForSearch = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName(ConfigFactory.load().getString("application.name")).build();

            return youtubeForSearch;
        }catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
