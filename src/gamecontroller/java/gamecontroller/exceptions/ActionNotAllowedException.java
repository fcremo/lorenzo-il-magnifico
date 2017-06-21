package gamecontroller.exceptions;

/**
 * This exceptions signals that the player attempted to perform an action that was not allowed
 */
public class ActionNotAllowedException extends Exception {
    public ActionNotAllowedException() {
    }

    public ActionNotAllowedException(String s) {
        super(s);
    }

    public ActionNotAllowedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ActionNotAllowedException(Throwable throwable) {
        super(throwable);
    }

    public ActionNotAllowedException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
