package com.migration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MigrationIssue implements Serializable {
    private String id;
    private String type;
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    private String title;
    private String description;
    private String filePath;
    private int lineNumber;
    private String currentCode;
    private String suggestedFix;
    private String explanation;
    private boolean autoFixable;
    private List<String> affectedModules;
    
    public MigrationIssue() {
        this.affectedModules = new ArrayList<>();
    }
    
    public MigrationIssue(String id, String type, String severity, String title, 
                         String description, String filePath, int lineNumber) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.lineNumber = lineNumber;
        this.affectedModules = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    
    public String getCurrentCode() { return currentCode; }
    public void setCurrentCode(String currentCode) { this.currentCode = currentCode; }
    
    public String getSuggestedFix() { return suggestedFix; }
    public void setSuggestedFix(String suggestedFix) { this.suggestedFix = suggestedFix; }
    
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    
    public boolean isAutoFixable() { return autoFixable; }
    public void setAutoFixable(boolean autoFixable) { this.autoFixable = autoFixable; }
    
    public List<String> getAffectedModules() { return affectedModules; }
    public void setAffectedModules(List<String> affectedModules) { this.affectedModules = affectedModules; }
}
