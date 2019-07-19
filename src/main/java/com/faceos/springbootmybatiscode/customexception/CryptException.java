package com.faceos.springbootmybatiscode.customexception;

/**
 * CryptException
 * 自定义异常
 *
 * @author lang
 * @date 2019-07-08
 */
public class CryptException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CryptException() {
    }

    public CryptException(String msg, Throwable e) {
        super(msg, e);
    }

    public CryptException(String msg) {
        super(msg);
    }

    public CryptException(Throwable cause) {
        super(cause);
    }
}
