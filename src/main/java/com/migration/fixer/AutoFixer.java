package com.migration.fixer;

import com.migration.model.MigrationIssue;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class AutoFixer {
    
    public FixResult applyFix(MigrationIssue issue) {
        FixResult result = new FixResult();
        result.setIssueId(issue.getId());
        result.setFilePath(issue.getFilePath());
        
        if (!issue.isAutoFixable()) {
            result.setSuccess(false);
            result.setMessage("This issue is not auto-fixable and requires manual intervention.");
            return result;
        }
        
        try {
            Path filePath = Paths.get(issue.getFilePath());
            if (!Files.exists(filePath)) {
                result.setSuccess(false);
                result.setMessage("File not found: " + issue.getFilePath());
                return result;
            }
            
            // Create backup
            String backupPath = createBackup(filePath);
            result.setBackupPath(backupPath);
            
            // Read file content
            String content = Files.readString(filePath);
            String originalContent = content;
            
            // Apply fix based on issue type
            String fixedContent = applyFixByType(content, issue);
            
            if (fixedContent.equals(originalContent)) {
                result.setSuccess(false);
                result.setMessage("No changes were made. The pattern might have already been fixed.");
                return result;
            }
            
            // Write fixed content
            Files.writeString(filePath, fixedContent);
            
            result.setSuccess(true);
            result.setMessage("Successfully applied fix to " + filePath.getFileName());
            result.setOriginalCode(issue.getCurrentCode());
            result.setFixedCode(issue.getSuggestedFix());
            
        } catch (IOException e) {
            result.setSuccess(false);
            result.setMessage("Error applying fix: " + e.getMessage());
        }
        
        return result;
    }
    
    public BatchFixResult applyMultipleFixes(List<MigrationIssue> issues) {
        BatchFixResult batchResult = new BatchFixResult();
        List<FixResult> results = new ArrayList<>();
        
        int successCount = 0;
        int failureCount = 0;
        
        for (MigrationIssue issue : issues) {
            FixResult result = applyFix(issue);
            results.add(result);
            
            if (result.isSuccess()) {
                successCount++;
            } else {
                failureCount++;
            }
        }
        
        batchResult.setResults(results);
        batchResult.setTotalProcessed(issues.size());
        batchResult.setSuccessCount(successCount);
        batchResult.setFailureCount(failureCount);
        
        return batchResult;
    }
    
    private String applyFixByType(String content, MigrationIssue issue) {
        String currentCode = issue.getCurrentCode();
        String suggestedFix = issue.getSuggestedFix();
        
        // TODO: Add fix implementations for other issue types
        switch (issue.getType()) {
            case "DEPRECATED_CONFIGURATIONS":
                return fixDeprecatedConfigurations(content, currentCode, suggestedFix);
                
            default:
                // Generic replacement
                return content.replace(currentCode, suggestedFix);
        }
    }
    
    private String fixDeprecatedConfigurations(String content, String currentCode, String suggestedFix) {
        // Replace all occurrences of deprecated configurations
        String result = content;
        
        // Handle both space-separated and parentheses-based syntax
        result = result.replaceAll("\\bcompile\\s+", "implementation ");
        result = result.replaceAll("\\bruntime\\s+", "runtimeOnly ");
        result = result.replaceAll("\\btestCompile\\s+", "testImplementation ");
        result = result.replaceAll("\\btestRuntime\\s+", "testRuntimeOnly ");
        
        return result;
    }
    
    // TODO: Implement fix methods for other issue types:
    //
    // private String fixDeprecatedDependencyConfig(String content, String currentCode, String suggestedFix) {
    //     // Replace compile() with implementation(), etc.
    // }
    //
    // private String fixArchiveProperties(String content, String currentCode, String suggestedFix) {
    //     // Replace archiveName = with archiveFileName.set()
    // }
    //
    // private String fixTaskLeftShift(String content, String currentCode) {
    //     // Replace task << with task { doLast
    // }
    //
    // private String fixDeprecatedMethods(String content, String currentCode, String suggestedFix) {
    //     // Replace getArchivePath() with archiveFile.get()
    // }
    //
    // private String fixSourceSetOutput(String content, String currentCode, String suggestedFix) {
    //     // Replace classesDir with classesDirs
    // }
    //
    // private String fixDeprecatedProperties(String content, String currentCode, String suggestedFix) {
    //     // Replace archivesBaseName = with base { archivesName.set() }
    // }
    //
    // private String fixDeprecatedAPI(String content, String currentCode) {
    //     // Replace convention API with extensions API
    // }
    
    private String createBackup(Path filePath) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Path backupPath = Paths.get(filePath.toString() + ".backup." + timestamp);
        Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        return backupPath.toString();
    }
    
    public boolean restoreBackup(String backupPath, String originalPath) {
        try {
            Path backup = Paths.get(backupPath);
            Path original = Paths.get(originalPath);
            
            if (Files.exists(backup)) {
                Files.copy(backup, original, StandardCopyOption.REPLACE_EXISTING);
                Files.delete(backup);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error restoring backup: " + e.getMessage());
        }
        return false;
    }
    
    public static class FixResult {
        private String issueId;
        private String filePath;
        private boolean success;
        private String message;
        private String backupPath;
        private String originalCode;
        private String fixedCode;
        
        // Getters and Setters
        public String getIssueId() { return issueId; }
        public void setIssueId(String issueId) { this.issueId = issueId; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getBackupPath() { return backupPath; }
        public void setBackupPath(String backupPath) { this.backupPath = backupPath; }
        
        public String getOriginalCode() { return originalCode; }
        public void setOriginalCode(String originalCode) { this.originalCode = originalCode; }
        
        public String getFixedCode() { return fixedCode; }
        public void setFixedCode(String fixedCode) { this.fixedCode = fixedCode; }
    }
    
    public static class BatchFixResult {
        private List<FixResult> results;
        private int totalProcessed;
        private int successCount;
        private int failureCount;
        
        public BatchFixResult() {
            this.results = new ArrayList<>();
        }
        
        // Getters and Setters
        public List<FixResult> getResults() { return results; }
        public void setResults(List<FixResult> results) { this.results = results; }
        
        public int getTotalProcessed() { return totalProcessed; }
        public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }
        
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
    }
}
