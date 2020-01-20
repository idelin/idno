package win.delin.idcards.Exception;

/**
 * 身份证 15位 转换到 18位 过程异常
 * 无法转换
 * @author delin
 * @date 2020/1/20
 */
public class IdCard15Cast18Exception extends IdCardException {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public IdCard15Cast18Exception() {
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
    public IdCard15Cast18Exception(String message) {
        super(message);
    }
}
