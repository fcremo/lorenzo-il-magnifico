package ui.cli.exceptions;

/**
 * This exception signals that the user tried to execute an invalid command
 */
public class InvalidCommandException extends Exception {
public InvalidCommandException(String s) {
        super(s);
    }

    public InvalidCommandException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidCommandException(Throwable throwable) {
        super(throwable);
    }

    public InvalidCommandException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
