package youtube;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import models.ChannelDetails;
import models.ChannelRequest;
import models.ChannelResponse;
import play.mvc.Result;
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

    private final static String ALREADY_SUBSCRIBED = "The subscription that you are trying to create already exists.";

    public enum SUBSCRIPTION_STATUS {
        SUBSCRIBED, UNSUBSCRIBED, ALREADY_SUBSCRIBED, FAILED_SUBSCRIBE, NOT_SUBSCRIBED
    }

    public static SubscriptionListResponse list(ChannelRequest channelRequest) throws CredentialRequiredException{
        try {
            // Define the API request for retrieving search results.
            //YouTube.Channels.List channelsList  = YoutubeConnector.getConnection().channels().list("contentDetails");
            //channelsList.setMine(true);
            //channelsList.setMaxResults(channelRequest.recordsPerPage);
            //channelsList.setFields("items(contentDetails,id,kind,snippet,topicDetails),kind,nextPageToken,pageInfo,prevPageToken,tokenPagination");
            //ChannelListResponse channelListResponse = channelsList.execute();


            YouTube.Subscriptions.List subscriptionsList = YoutubeConnector.getConnection().subscriptions().list("snippet,contentDetails");
            subscriptionsList.setMine(true);
            SubscriptionListResponse channelListResponse = subscriptionsList.execute();

            return channelListResponse;
        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
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


    public static Subscription alreadySubscribed(String channelId) throws CredentialRequiredException {
        try {

            System.out.println("in alreadySubscribed "+channelId);
            YouTube.Subscriptions.List subscriptionsList = YoutubeConnector.getConnection().subscriptions().list("snippet,contentDetails,subscriberSnippet");
            //subscriptionsList.setMine(true);
            subscriptionsList.setChannelId(channelId);
            //subscriptionsList.setForChannelId(channelId);

            SubscriptionListResponse channelListResponse = subscriptionsList.execute();

            if(channelListResponse !=null) {
                if(channelListResponse.getItems() != null && channelListResponse.getItems().size() == 1) {
                    System.out.println("in alreadySubscribed subscribed "+channelId);
                    return channelListResponse.getItems().get(0);
                }
            }

            return null;
        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();;
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

    public static Channel getChannelByUserName(String userName) throws CredentialRequiredException {
        try {
            // Define the API request for retrieving search results.

            YouTube.Channels.List channelsList = YoutubeConnector.getConnection().channels().list("snippet,contentDetails");
            channelsList.setForUsername(userName);

            ChannelListResponse channelListResponse = channelsList.execute();
            if(channelListResponse !=null) {

                List<Channel> channels = channelListResponse.getItems();
                if(channels !=null && channels.size() ==1 ) {
                    return channels.get(0);
                } else {
                    System.out.println("empty channels");
                }

            }


        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
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

    public static Subscription getChannelByChannelId(String channelId) throws CredentialRequiredException {
        try {
            // Define the API request for retrieving search results.

            YouTube.Subscriptions.List subscriptionsList = YoutubeConnector.getConnection().subscriptions().list("snippet,contentDetails");
            subscriptionsList.setMine(true);
            subscriptionsList.setChannelId(channelId);
            SubscriptionListResponse channelListResponse = subscriptionsList.execute();

            if(channelListResponse !=null) {
                if(channelListResponse.getItems() !=null && channelListResponse.getItems().size() ==1) {
                    return channelListResponse.getItems().get(0);
                }
            }


        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
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

    public static Subscription subscribe(String channelId) throws CredentialRequiredException {
        try {
            // Define the API request for retrieving search results.
            //YouTube.Channels.List channelsList  = YoutubeConnector.getConnection().channels().list("contentDetails");
            //channelsList.setMine(true);
            //channelsList.setMaxResults(channelRequest.recordsPerPage);
            //channelsList.setFields("items(contentDetails,id,kind,snippet,topicDetails),kind,nextPageToken,pageInfo,prevPageToken,tokenPagination");
            //ChannelListResponse channelListResponse = channelsList.execute();


                System.out.println("subscribe to channel "+channelId);
                // Create a resourceId that identifies the channel ID.
                ResourceId resourceId = new ResourceId();
                resourceId.setChannelId(channelId);
                resourceId.setKind("youtube#channel");

                // Create a snippet that contains the resourceId.
                SubscriptionSnippet snippet = new SubscriptionSnippet();
                snippet.setResourceId(resourceId);


                // Create a request to add the subscription and send the request.
                // The request identifies subscription metadata to insert as well
                // as information that the API server should return in its response.
                Subscription subscription = new Subscription();
                subscription.setSnippet(snippet);
                YouTube.Subscriptions.Insert subscriptionInsert =
                        YoutubeConnector.getConnection().subscriptions().insert("snippet,contentDetails", subscription);
                Subscription returnedSubscription = subscriptionInsert.execute();

                return returnedSubscription;



        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
            if(e.getDetails().getMessage().equalsIgnoreCase(ALREADY_SUBSCRIBED)) {
                Subscription sub = YChannel.getChannelByChannelId(channelId);
                if(sub!=null) {
                    return sub;
                }
            }
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
