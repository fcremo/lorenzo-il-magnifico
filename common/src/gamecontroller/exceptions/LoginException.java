package gamecontroller.exceptions;

/**
 * This exception is thrown if the user tries to login using an already taken username
 */
public class LoginException extends Exception {
    public LoginException() {
        // Empty constructor
    }

    public LoginException(String s) {
        super(s);
    }

    public LoginException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public LoginException(Throwable throwable) {
        super(throwable);
    }

    public LoginException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
