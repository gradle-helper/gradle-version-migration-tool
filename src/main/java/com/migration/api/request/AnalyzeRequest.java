package com.migration.api.request;

/**
 * Request object for project analysis
 */
public class AnalyzeRequest {
    private String projectPath;
    
    public AnalyzeRequest() {
    }
    
    public AnalyzeRequest(String projectPath) {
        this.projectPath = projectPath;
    }
    
    public String getProjectPath() {
        return projectPath;
    }
    
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
