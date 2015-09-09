import controllers.UserController;
import models.User;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 *
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 *
 */
public class UserControllerTest {

    @Test
    public void testUserLogin (){

        running(fakeApplication(), new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.email = "murali.konusu@gmail.com";
                user.password = "india";

                User authUser = UserController.authenticate(user);
                assertThat (authUser.email != null);
                assertThat(authUser.userName.equals("Murali Konusu"));
            }
        });
    }
}
