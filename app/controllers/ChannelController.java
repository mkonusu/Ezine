package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.typesafe.config.ConfigFactory;
import models.ChannelRequest;
import models.ChannelResponse;

import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import youtube.YChannel;
import youtube.util.CredentialRequiredException;
import youtube.util.ResponseMapper;


/**
 * @Author Murali Konusu
 */
public class ChannelController extends Controller {

    ChannelResponse channelResponse = null;

    public static Result listChannels() {

        ChannelResponse response = null;
        String SUPERUSER = request().getQueryString("SUPERUSER");
        try {
            ObjectMapper om = new ObjectMapper();
            ChannelRequest channelRequest;
            if (request().body() == null || request().body().asJson() == null) {

                    channelRequest = new ChannelRequest(SearchController.DEFAULT_RECORDS_PER_PAGE);

            } else {
                JsonNode json = request().body().asJson();
                channelRequest = new Gson().fromJson(json.toString(), models.ChannelRequest.class);
            }


            response = ResponseMapper.getChannelResponse(YChannel.list(channelRequest));

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

}
