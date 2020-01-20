package win.delin.idcards.Exception;

/**
 * 身份证号长度错误异常
 * 合法长度 15 or 18
 * @author delin
 * @date 2020/1/20
 */
public class IdCardLengthErrorException extends IdCardException {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public IdCardLengthErrorException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public IdCardLengthErrorException(String message) {
        super(message);
    }
}
