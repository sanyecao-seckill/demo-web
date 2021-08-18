package com.demo.exception;

public class BizException extends Exception{

    private String errorCode;

    public BizException(String errorCode,String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BizException(String errorCode,String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BizException(String message) {
        super(message);
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
