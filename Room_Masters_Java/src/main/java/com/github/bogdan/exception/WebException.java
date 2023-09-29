package com.github.bogdan.exception;

public class WebException extends RuntimeException{
    private final int status;
    public WebException(String message,int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
