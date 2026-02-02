package com.githubrepoexplorerbackend.exception;

public class OAuthExchangeException extends RuntimeException {
    public OAuthExchangeException(String message) {
        super(message);
    }

    public OAuthExchangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
