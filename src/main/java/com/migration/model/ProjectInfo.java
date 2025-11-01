package com.migration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProjectInfo implements Serializable {
    private String projectPath;
    private String projectName;
    private String currentGradleVersion;
    private boolean isMultiModule;
    private List<String> modules;
    private List<MigrationIssue> issues;
    private int totalIssues;
    private int criticalIssues;
    private int autoFixableIssues;
    
    public ProjectInfo() {
        this.modules = new ArrayList<>();
        this.issues = new ArrayList<>();
    }

    // Getters and Setters
    public String getProjectPath() { return projectPath; }
    public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
    
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
    
    public String getCurrentGradleVersion() { return currentGradleVersion; }
    public void setCurrentGradleVersion(String currentGradleVersion) { 
        this.currentGradleVersion = currentGradleVersion; 
    }
    
    public boolean isMultiModule() { return isMultiModule; }
    public void setMultiModule(boolean multiModule) { isMultiModule = multiModule; }
    
    public List<String> getModules() { return modules; }
    public void setModules(List<String> modules) { this.modules = modules; }
    
    public List<MigrationIssue> getIssues() { return issues; }
    public void setIssues(List<MigrationIssue> issues) { this.issues = issues; }
    
    public int getTotalIssues() { return totalIssues; }
    public void setTotalIssues(int totalIssues) { this.totalIssues = totalIssues; }
    
    public int getCriticalIssues() { return criticalIssues; }
    public void setCriticalIssues(int criticalIssues) { this.criticalIssues = criticalIssues; }
    
    public int getAutoFixableIssues() { return autoFixableIssues; }
    public void setAutoFixableIssues(int autoFixableIssues) { 
        this.autoFixableIssues = autoFixableIssues; 
    }
}
