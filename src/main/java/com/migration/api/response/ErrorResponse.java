package com.migration.api.response;

/**
 * Standard error response for API endpoints
 */
public class ErrorResponse {
    private String error;
    
    public ErrorResponse() {
    }
    
    public ErrorResponse(String error) {
        this.error = error;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
