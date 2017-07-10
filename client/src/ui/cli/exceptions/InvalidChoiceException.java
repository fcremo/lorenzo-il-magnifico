package ui.cli.exceptions;

/**
 * This exception signals that the user performed an invalid choice
 */
public class InvalidChoiceException extends Exception {
    public InvalidChoiceException(String s) {
        super(s);
    }

    public InvalidChoiceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidChoiceException(Throwable throwable) {
        super(throwable);
    }

    public InvalidChoiceException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
