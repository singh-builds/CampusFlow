package com.campusflow.util;

/** Business-rule error (bad input, duplicate token, wrong role...). Message is shown to the user. */
public class AppException extends RuntimeException {
    public AppException(String message) { super(message); }
    public AppException(String message, Throwable cause) { super(message, cause); }
}
