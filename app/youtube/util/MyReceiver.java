package youtube.util;

import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by home on 8/23/2015.
 */
public class MyReceiver  implements VerificationCodeReceiver {
    private static final String CALLBACK_PATH = "/Callback";
    //private Server server;
    String code;
    String error;
    final Lock lock;
    final Condition gotAuthorizationResponse;
    private int port;
    private final String host;

    public MyReceiver() {
        this("localhost", -1);
    }

    MyReceiver(String host, int port) {
        this.lock = new ReentrantLock();
        this.gotAuthorizationResponse = this.lock.newCondition();
        this.host = host;
        this.port = port;
    }

    public String getRedirectUri() throws IOException {
        if (this.port == -1) {
            return "https://" + this.host + "/Callback";
        }

        return "http://" + this.host + ":" + this.port + "/Callback";
    }

    public String waitForCode() throws IOException {
        this.lock.lock();

        String var1;
        try {
            while (this.code == null && this.error == null) {
                this.gotAuthorizationResponse.awaitUninterruptibly();
            }

            if (this.error != null) {
                throw new IOException("User authorization failed (" + this.error + ")");
            }

            var1 = this.code;
        } finally {
            this.lock.unlock();
        }

        return var1;
    }

    public void stop() throws IOException {


    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    private static int getUnusedPort() throws IOException {
        Socket s = new Socket();
        s.bind((SocketAddress) null);

        int var1;
        try {
            var1 = s.getLocalPort();
        } finally {
            s.close();
        }

        return var1;
    }

    class CallbackHandler extends AbstractHandler {
        CallbackHandler() {
        }

        public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException {
            if ("/Callback".equals(target)) {
                this.writeLandingHtml(response);
                response.flushBuffer();
                ((Request) request).setHandled(true);
                MyReceiver.this.lock.lock();

                try {
                    MyReceiver.this.error = request.getParameter("error");
                    MyReceiver.this.code = request.getParameter("code");
                    MyReceiver.this.gotAuthorizationResponse.signal();
                } finally {
                    MyReceiver.this.lock.unlock();
                }

            }
        }

        private void writeLandingHtml(HttpServletResponse response) throws IOException {
            response.setStatus(200);
            response.setContentType("text/html");
            PrintWriter doc = response.getWriter();
            doc.println("<html>");
            doc.println("<head><title>OAuth 2.0 Authentication Token Recieved</title></head>");
            doc.println("<body>");
            doc.println("Received verification code. Closing...");
            doc.println("<script type=\'text/javascript\'>");
            doc.println("window.setTimeout(function() {");
            doc.println("    window.open(\'\', \'_self\', \'\'); window.close(); }, 1000);");
            doc.println("if (window.opener) { window.opener.checkToken(); }");
            doc.println("</script>");
            doc.println("</body>");
            doc.println("</HTML>");
            doc.flush();
        }
    }

    public static final class Builder {
        private String host = "localhost";
        private int port = -1;

        public Builder() {
        }

        public MyReceiver build() {
            return new MyReceiver(this.host, this.port);
        }

        public String getHost() {
            return this.host;
        }

        public MyReceiver.Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public int getPort() {
            return this.port;
        }

        public MyReceiver.Builder setPort(int port) {
            this.port = port;
            return this;
        }
    }
}