package com.migration.detector;

import com.migration.model.MigrationIssue;
import com.migration.model.ProjectInfo;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class GradleIssueDetector {
    
    private static final Map<String, IssuePattern> ISSUE_PATTERNS = new HashMap<>();
    
    static {
        initializeIssuePatterns();
    }
    
    private static void initializeIssuePatterns() {
        // Example Pattern: Deprecated compile/runtime configurations
        ISSUE_PATTERNS.put("DEPRECATED_CONFIGURATIONS", new IssuePattern(
            Pattern.compile("\\b(compile|runtime|testCompile|testRuntime)\\s+"),
            "CRITICAL",
            "Deprecated Configuration Usage",
            "The 'compile', 'runtime', 'testCompile', and 'testRuntime' configurations are removed in Gradle 9. " +
            "These must be replaced with 'implementation', 'runtimeOnly', 'testImplementation', and 'testRuntimeOnly' respectively.",
            true
        ));
        
        // TODO: Add more detection patterns:
        // 
        // 1. Deprecated Convention API
        //    Pattern: \\bconvention\\s*\\.\\s*getPlugin|\\bconvention\\s*\\[
        //    Severity: HIGH
        //    Description: Convention API removed, use Extensions API
        //
        // 2. Archive Task Properties
        //    Pattern: \\barchiveName\\s*=|\\barchiveBaseName\\s*=
        //    Severity: HIGH
        //    Description: Use Property API (archiveFileName.set())
        //
        // 3. Gradle Version Check
        //    Pattern: distributionUrl=.*gradle-(\\d+\\.\\d+)
        //    Severity: CRITICAL
        //    Description: Check if Gradle version < 9.0
        //
        // 4. Task Left-Shift Operator
        //    Pattern: task\\s+\\w+\\s*<<
        //    Severity: HIGH
        //    Description: << operator removed, use doLast { }
        //
        // 5. Deprecated API Methods
        //    Pattern: \\b(getArchivePath|getClassesDir|getDestinationDir)\\s*\\(
        //    Severity: HIGH
        //    Description: Use Property API equivalents
        //
        // 6. Deprecated Dependency Configuration Methods
        //    Pattern: (compile|runtime|testCompile|testRuntime)\\s*\\(
        //    Severity: CRITICAL
        //    Description: Use implementation(), runtimeOnly(), etc.
        //
        // 7. SourceSet Output Properties
        //    Pattern: sourceSets\\.\\w+\\.output\\.classesDir
        //    Severity: HIGH
        //    Description: Use classesDirs (plural)
        //
        // 8. Deprecated Task Types
        //    Pattern: \\b(Upload|InstallTask)\\b
        //    Severity: HIGH
        //    Description: Use maven-publish or ivy-publish plugins
        //
        // 9. Dynamic Properties
        //    Pattern: \\bproject\\.ext\\[|\\bext\\[
        //    Severity: MEDIUM
        //    Description: Consider typed extensions
        //
        // 10. Legacy Buildscript Classpath
        //     Pattern: buildscript\\s*\\{[^}]*classpath\\s+['\"]
        //     Severity: MEDIUM
        //     Description: Migrate to plugins {} block
    }
    
    public ProjectInfo analyzeProject(String projectPath) throws IOException {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setProjectPath(projectPath);
        
        Path rootPath = Paths.get(projectPath);
        projectInfo.setProjectName(rootPath.getFileName().toString());
        
        // Check if multi-module
        Path settingsFile = rootPath.resolve("settings.gradle");
        if (!Files.exists(settingsFile)) {
            settingsFile = rootPath.resolve("settings.gradle.kts");
        }
        
        if (Files.exists(settingsFile)) {
            String content = Files.readString(settingsFile);
            List<String> modules = extractModules(content);
            projectInfo.setModules(modules);
            projectInfo.setMultiModule(!modules.isEmpty());
        }
        
        // Detect Gradle version
        Path wrapperProperties = rootPath.resolve("gradle/wrapper/gradle-wrapper.properties");
        if (Files.exists(wrapperProperties)) {
            String content = Files.readString(wrapperProperties);
            projectInfo.setCurrentGradleVersion(extractGradleVersion(content));
        }
        
        // Scan for issues
        List<MigrationIssue> issues = new ArrayList<>();
        scanDirectory(rootPath, issues, projectInfo);
        
        projectInfo.setIssues(issues);
        projectInfo.setTotalIssues(issues.size());
        projectInfo.setCriticalIssues((int) issues.stream()
            .filter(i -> "CRITICAL".equals(i.getSeverity())).count());
        projectInfo.setAutoFixableIssues((int) issues.stream()
            .filter(MigrationIssue::isAutoFixable).count());
        
        return projectInfo;
    }
    
    private void scanDirectory(Path directory, List<MigrationIssue> issues, ProjectInfo projectInfo) throws IOException {
        Files.walk(directory)
            .filter(path -> {
                String fileName = path.getFileName().toString();
                return (fileName.endsWith(".gradle") || fileName.endsWith(".gradle.kts")) &&
                       !path.toString().contains("/.gradle/") &&
                       !path.toString().contains("/build/");
            })
            .forEach(path -> {
                try {
                    scanFile(path, issues, projectInfo);
                } catch (IOException e) {
                    System.err.println("Error scanning file: " + path + " - " + e.getMessage());
                }
            });
    }
    
    private void scanFile(Path filePath, List<MigrationIssue> issues, ProjectInfo projectInfo) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        String content = String.join("\n", lines);
        
        for (Map.Entry<String, IssuePattern> entry : ISSUE_PATTERNS.entrySet()) {
            String issueType = entry.getKey();
            IssuePattern pattern = entry.getValue();
            
            Matcher matcher = pattern.pattern.matcher(content);
            int issueCount = 0;
            
            while (matcher.find() && issueCount < 100) { // Limit per pattern per file
                int lineNumber = getLineNumber(content, matcher.start());
                String matchedText = matcher.group();
                
                MigrationIssue issue = new MigrationIssue();
                issue.setId(UUID.randomUUID().toString());
                issue.setType(issueType);
                issue.setSeverity(pattern.severity);
                issue.setTitle(pattern.title);
                issue.setDescription(pattern.description);
                issue.setFilePath(filePath.toString());
                issue.setLineNumber(lineNumber);
                issue.setCurrentCode(matchedText.trim());
                issue.setExplanation(generateDetailedExplanation(issueType, matchedText));
                issue.setAutoFixable(pattern.autoFixable);
                issue.setSuggestedFix(generateSuggestedFix(issueType, matchedText));
                
                // Track affected modules
                String moduleName = getModuleName(filePath, projectInfo);
                if (moduleName != null) {
                    issue.getAffectedModules().add(moduleName);
                }
                
                issues.add(issue);
                issueCount++;
            }
        }
    }
    
    private String getModuleName(Path filePath, ProjectInfo projectInfo) {
        String pathStr = filePath.toString();
        String projectPath = projectInfo.getProjectPath();
        
        if (pathStr.startsWith(projectPath)) {
            String relativePath = pathStr.substring(projectPath.length());
            String[] parts = relativePath.split(File.separator);
            if (parts.length > 1 && !parts[1].isEmpty()) {
                return parts[1];
            }
        }
        return "root";
    }
    
    private int getLineNumber(String content, int position) {
        return content.substring(0, position).split("\n").length;
    }
    
    private String extractGradleVersion(String wrapperContent) {
        Pattern pattern = Pattern.compile("gradle-(\\d+\\.\\d+(?:\\.\\d+)?(?:-\\w+)?)");
        Matcher matcher = pattern.matcher(wrapperContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown";
    }
    
    private List<String> extractModules(String settingsContent) {
        List<String> modules = new ArrayList<>();
        Pattern pattern = Pattern.compile("include\\s*\\(?['\"]([^'\"]+)['\"]");
        Matcher matcher = pattern.matcher(settingsContent);
        
        while (matcher.find()) {
            modules.add(matcher.group(1).replace(":", ""));
        }
        
        return modules;
    }
    
    private String generateDetailedExplanation(String issueType, String matchedText) {
        // TODO: Add detailed explanations for other issue types
        switch (issueType) {
            case "DEPRECATED_CONFIGURATIONS":
                return "Gradle 9 has removed the legacy dependency configurations. The matched code '" + matchedText + 
                       "' uses a deprecated configuration. This will cause build failures. " +
                       "Migration: compile → implementation, runtime → runtimeOnly, " +
                       "testCompile → testImplementation, testRuntime → testRuntimeOnly.";
                       
            default:
                return "This code pattern is deprecated or removed in Gradle 9 and requires migration.";
        }
    }
    
    private String generateSuggestedFix(String issueType, String matchedText) {
        // TODO: Add suggested fixes for other issue types
        switch (issueType) {
            case "DEPRECATED_CONFIGURATIONS":
                if (matchedText.contains("compile ")) {
                    return matchedText.replace("compile ", "implementation ");
                } else if (matchedText.contains("runtime ")) {
                    return matchedText.replace("runtime ", "runtimeOnly ");
                } else if (matchedText.contains("testCompile ")) {
                    return matchedText.replace("testCompile ", "testImplementation ");
                } else if (matchedText.contains("testRuntime ")) {
                    return matchedText.replace("testRuntime ", "testRuntimeOnly ");
                }
                break;
        }
        
        return "// TODO: Manual migration required - see explanation";
    }
    
    private static class IssuePattern {
        Pattern pattern;
        String severity;
        String title;
        String description;
        boolean autoFixable;
        
        IssuePattern(Pattern pattern, String severity, String title, String description, boolean autoFixable) {
            this.pattern = pattern;
            this.severity = severity;
            this.title = title;
            this.description = description;
            this.autoFixable = autoFixable;
        }
    }
}
