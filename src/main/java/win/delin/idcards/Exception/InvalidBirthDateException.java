package win.delin.idcards.Exception;

public class InvalidBirthDateException extends IdCardException {
    public InvalidBirthDateException(String message) {
        super(message);
    }

    public InvalidBirthDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
