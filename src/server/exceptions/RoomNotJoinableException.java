package server.exceptions;

/**
 * This exceptions signals that the player tried to join a room that is not joinable.
 * It's unchecked because the server knows when this exception can happen and catches it
 * only in that occasions.
 */
public class RoomNotJoinableException extends RuntimeException {
    public RoomNotJoinableException() {
    }

    public RoomNotJoinableException(String s) {
        super(s);
    }

    public RoomNotJoinableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RoomNotJoinableException(Throwable throwable) {
        super(throwable);
    }

    public RoomNotJoinableException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
