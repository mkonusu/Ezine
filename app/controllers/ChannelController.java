package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.youtube.model.Channel;
import com.google.gson.Gson;
import com.typesafe.config.ConfigFactory;
import models.ChannelDetails;
import models.ChannelRequest;
import models.ChannelResponse;

import models.User;
import org.apache.commons.lang3.StringUtils;
import org.jongo.MongoCollection;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import util.CollectionNames;
import youtube.YChannel;
import youtube.util.CredentialRequiredException;
import youtube.util.ResponseMapper;


/**
 * @Author Murali Konusu
 */
public class ChannelController extends Controller {

    ChannelResponse channelResponse = null;

    public static Result listChannels(String userId, String langCode) {

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

    @BodyParser.Of(BodyParser.Json.class)
    public static Result subscribe() {
        String SUPERUSER = request().getQueryString("SUPERUSER");
        JsonNode json = request().body().asJson();
        String channelId =  json.get("channelId").textValue();
        String langCode =  json.get("langCode").textValue();
        boolean channelByUser = false;
        String channelUser = null;
        try{
            if(channelId !=null && channelId.startsWith("/user/")) {
                channelByUser = true;
                String userName = channelId.substring(channelId.lastIndexOf("/")+1, channelId.length());
                System.out.println("Channel User Name "+userName);
                Channel channel = YChannel.getChannelByUserName(userName);
                if(channel != null) {
                    channelUser = userName;
                    channelId = channel.getId();
                    System.out.println("Channel id "+channelId );
                } else {
                    return ok("Channel does not exists with the name");
                }
            }


            ChannelDetails channelInfo = null;//ResponseMapper.getChannelResponse(YChannel.alreadySubscribed(channelId));
            if(channelInfo == null) {
                System.out.println("Channel not subscribed");
                channelInfo = ResponseMapper.getChannelResponse(YChannel.subscribe(channelId));
                System.out.println("Subscribe channel");
            } else {
                System.out.println("already subscribed");
            }

            // check if exists in db and store
            if(channelInfo != null) {
                if(!channelByUser) {
                     channelInfo.channelUserName = channelUser;
                }
                System.out.println("b4 store in db "+channelInfo.channelId);
                MongoCollection channels = MongoDBController.getCollection(CollectionNames.channels);
                ChannelDetails fromDB = channels.findOne("{channelId :#}", channelInfo.channelId).as(ChannelDetails.class);
                System.out.println("already exists in db "+fromDB);
                if(fromDB == null) {
                    System.out.println("store in db "+channelInfo.channelId);
                    channelInfo.language = langCode;
                    channels.insert(channelInfo);
                } else {
                    channelInfo._id=fromDB._id;
                    channels.save(channelInfo);
                }
                fromDB = channels.findOne("{channelId :#}", channelInfo.channelId).as(ChannelDetails.class);
                return ok(new Gson().toJson(fromDB));
            }

            return ok(new Gson().toJson(channelInfo));
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

            return ok("Success!");
        }
}
