package server.exceptions;

/**
 * This exceptions signals that the player tried to join a game that is not joinable.
 * It's unchecked because the server knows when this exception can happen and catches it
 * only in that occasions.
 */
public class GameNotJoinableException extends RuntimeException {
    public GameNotJoinableException() {
    }

    public GameNotJoinableException(String s) {
        super(s);
    }

    public GameNotJoinableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public GameNotJoinableException(Throwable throwable) {
        super(throwable);
    }

    public GameNotJoinableException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
