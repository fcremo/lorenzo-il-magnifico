package server.exceptions;

/**
 * This exception is thrown if there are no games available
 */
public class NoAvailableGamesException extends Exception {
    public NoAvailableGamesException() {
    }

    public NoAvailableGamesException(String s) {
        super(s);
    }

    public NoAvailableGamesException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoAvailableGamesException(Throwable throwable) {
        super(throwable);
    }

    public NoAvailableGamesException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
