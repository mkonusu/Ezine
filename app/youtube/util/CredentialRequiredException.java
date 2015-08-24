package youtube.util;

/**
 * Created by home on 8/24/2015.
 */
public class CredentialRequiredException extends Exception {


    private String message = null;

    public CredentialRequiredException() {
        super();
    }

    public CredentialRequiredException(String message) {
        super(message);
        this.message = message;
    }

    public CredentialRequiredException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
