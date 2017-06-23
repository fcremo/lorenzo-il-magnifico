package server.exceptions;

/**
 * This exception signals that the player attempted to do something during someone else's turn
 */
public class NotYourTurnException extends ActionNotAllowedException {
    public NotYourTurnException() {
        // Constructor with no message
    }

    public NotYourTurnException(String s) {
        super(s);
    }

    public NotYourTurnException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotYourTurnException(Throwable throwable) {
        super(throwable);
    }

    public NotYourTurnException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
