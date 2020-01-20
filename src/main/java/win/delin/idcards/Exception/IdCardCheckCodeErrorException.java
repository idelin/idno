package win.delin.idcards.Exception;

/**
 * 身份证号校验位匹配错误
 * @author delin
 * @date 2020/1/20
 */
public class IdCardCheckCodeErrorException extends IdCardException {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public IdCardCheckCodeErrorException() {
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
    public IdCardCheckCodeErrorException(String message) {
        super(message);
    }

    public IdCardCheckCodeErrorException(String cardNo, String var) {
        super("cardNo is [" + cardNo + "], realCheckCode must [" + var + "]");
    }
}
