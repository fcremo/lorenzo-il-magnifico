package server.exceptions;

import gamecontroller.exceptions.ActionNotAllowedException;

/**
 * This exception signals that the player attempted to choose an invalid leader card
 */
public class LeaderCardNotAvailableException extends ActionNotAllowedException {
    public LeaderCardNotAvailableException() {
        // Constructor with no message
    }

    public LeaderCardNotAvailableException(String s) {
        super(s);
    }

    public LeaderCardNotAvailableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public LeaderCardNotAvailableException(Throwable throwable) {
        super(throwable);
    }

    public LeaderCardNotAvailableException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
