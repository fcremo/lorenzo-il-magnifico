package gamecontroller.exceptions;

/**
 * This exception is thrown when the game controller gets a username
 * that does not exist/does not belong to the current game
 */
public class PlayerDoesNotExistException extends ActionNotAllowedException {
    public PlayerDoesNotExistException() {
    }

    public PlayerDoesNotExistException(String s) {
        super(s);
    }

    public PlayerDoesNotExistException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PlayerDoesNotExistException(Throwable throwable) {
        super(throwable);
    }

    public PlayerDoesNotExistException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
