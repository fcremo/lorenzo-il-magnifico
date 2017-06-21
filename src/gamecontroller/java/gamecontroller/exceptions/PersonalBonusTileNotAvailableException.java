package gamecontroller.exceptions;

/**
 * This exception signals that the player has tried to choose an invalid personal bonus tile
 */
public class PersonalBonusTileNotAvailableException extends ActionNotAllowedException {
    public PersonalBonusTileNotAvailableException() {
        // constructor without error message
    }

    public PersonalBonusTileNotAvailableException(String s) {
        super(s);
    }

    public PersonalBonusTileNotAvailableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PersonalBonusTileNotAvailableException(Throwable throwable) {
        super(throwable);
    }

    public PersonalBonusTileNotAvailableException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
