package com.example.common.exception;

/**
 * システムの例外です。
 */
public class SystemException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	/**
     * コンストラクタです。
     */
    public SystemException() {
        super();
    }

    /**
     * コンストラクタです。
     * @param message message
     * @param cause cause
     */
    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * コンストラクタです。
     * @param message message
     */
    public SystemException(String message) {
        super(message);
    }

    /**
     * コンストラクタです。
     * @param cause cause
     */
    public SystemException(Throwable cause) {
        super(cause);
    }

}
