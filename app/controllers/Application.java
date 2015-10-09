package controllers;

import com.google.api.client.auth.oauth2.Credential;
import com.typesafe.config.ConfigFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import youtube.util.MyAuth;

public class Application extends Controller {

    public static Result index() {

        return ok(index.render("Your new application is ready."));
    }

    public static Result callbackHandler() {


        try {
            String token = request().getQueryString("code");
            String redirectUri = ConfigFactory.load().getString("youtube.api.redirect.uri");
            Credential credential = MyAuth.setToken(token, redirectUri);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return ok("Success");
    }

}
