package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.services.youtube.model.Channel;
import com.google.gson.Gson;
import com.typesafe.config.ConfigFactory;
import models.*;

import org.apache.commons.lang3.StringUtils;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import util.AppUtil;
import util.CollectionNames;
import youtube.YChannel;
import youtube.util.CredentialRequiredException;
import youtube.util.ResponseMapper;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;


/**
 * @Author Murali Konusu
 */
public class ChannelController extends Controller {



    public static Result getChannels() {

        ChannelResponse response = new ChannelResponse();

       try {

            ChannelRequest channelRequest = null;
            if (request().body() == null || request().body().asJson() == null) {
               // throw exception
                return ok("invalid request");
            } else {
                JsonNode json = request().body().asJson();
                channelRequest = new Gson().fromJson(json.toString(), models.ChannelRequest.class);
            }
            if(channelRequest == null) {
                return ok("Invalid request");
            }
           if(channelRequest.pagination == null) {
               channelRequest.pagination =  new Pagination(10, 1);
           }
            String userId = null;
            if(channelRequest!=null && channelRequest.userToken != null) {
                MongoCollection users = MongoDBController.getCollection(CollectionNames.users);
                User user = users.findOne("{ _id:#}", channelRequest.userToken).as(User.class);
                if(user != null ) userId = user._id;
            }
            MongoCollection channels = MongoDBController.getCollection(CollectionNames.channels);
            List<ChannelDetails> channelsList = new ArrayList<>();
            Pagination pagination  = channelRequest.pagination;
            try {
                MongoCursor<ChannelDetails> cursor = channels.find("{language : #}", channelRequest.language)
                    .skip(pagination.recordsPerPage * (pagination.pageNo - 1)).limit(pagination.recordsPerPage)
                    .as(ChannelDetails.class);

                while (cursor.hasNext()){
                    ChannelDetails resp = cursor.next();
                    if(userId !=null)  {
                        resp.isFavourite = AppUtil.isFavourite(userId, resp.channelId, Favourite.ResourceType.CHANNEL.toString());
                    }
                    channelsList.add(resp);
                }
            } catch (Exception e){
                Logger.error("Error processing jobs : " + e.getMessage());
            }

            long channelsCount = channels.count("{language : #}", channelRequest.language);
            response.subscribedChannels = channelsList;
            response.pagination = channelRequest.pagination;
            response.pagination.totalRecords = channelsCount;

        }  catch(Exception e) {
            e.printStackTrace();

        }

        return ok(new Gson().toJson(response));
    }

    public static Result youtubeLogin(String channelId) {

        Logger.info("in youtube login");
        ChannelDetails response = null;
        String SUPERUSER = request().getQueryString("SUPERUSER");
        try {
            Logger.info("in youtube login "+SUPERUSER);
            response = ResponseMapper.getChannelResponse(YChannel.alreadySubscribed(channelId));

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
            Logger.info("in youtube login falied "+e.getMessage());
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
        String userToken =  json.get("userToken").textValue();
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


            ChannelDetails channelInfo = ResponseMapper.getChannelResponse(YChannel.alreadySubscribed(channelId));
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
                channelInfo.language = langCode;
                channelInfo.active = true;

                if(fromDB == null) {
                    System.out.println("store in db " + channelInfo.channelId);
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

    public static Result getCategories() {

        MongoCollection categories = MongoDBController.getCollection(CollectionNames.categories);
        List<Category> categoriesList = new ArrayList<>();
        try (MongoCursor<Category> cursor = categories.find("{isActive : #}", true).as(Category.class)){
            while (cursor.hasNext()){
                categoriesList.add(cursor.next());
            }
        } catch (IOException e){
            Logger.error("Error getting categories : " + e.getMessage());
        }
        return ok(new Gson().toJson(categoriesList));
    }

    public static Result getFavourites() {

        String userId = "";
        MongoCollection favourites = MongoDBController.getCollection(CollectionNames.favourites);
        List<Favourite> favoritesList = new ArrayList<>();
        try (MongoCursor<Favourite> cursor = favourites.find("{ userId: #, #isActive : #}", userId, true).as(Favourite.class)){
            while (cursor.hasNext()){
                favoritesList.add(cursor.next());
            }
        } catch (IOException e){
            Logger.error("Error getting categories : " + e.getMessage());
        }
        return ok(new Gson().toJson(favoritesList));
    }

    public static Result setFavourite() {

        Favourite favRequest = null;
        if (request().body() == null || request().body().asJson() == null) {
            // throw exception
            return ok("invalid request");
        } else {
            JsonNode json = request().body().asJson();
            favRequest = new Gson().fromJson(json.toString(), models.Favourite.class);
        }
        if(favRequest !=null) {
            MongoCollection favourites = MongoDBController.getCollection(CollectionNames.favourites);
            Favourite fav = favourites.findOne("{{userId:#, resourceId:#, resourceType:#}}", favRequest.userId, favRequest.resourceId, favRequest.resourceType).as(Favourite.class);
            if (fav == null) {
                fav = new Favourite(favRequest.userId, favRequest.resourceId, favRequest.resourceType);
                favourites.insert(fav);
            } else {
                if (!fav.isActive) {
                    fav.isActive = true;
                    favourites.save(fav);
                }
            }
        }
        return ok("Success");
    }
}
