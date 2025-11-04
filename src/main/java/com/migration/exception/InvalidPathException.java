package com.migration.exception;

/**
 * Exception thrown when an invalid project path is provided
 */
public class InvalidPathException extends Exception {
    
    public InvalidPathException(String message) {
        super(message);
    }
    
    public InvalidPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
