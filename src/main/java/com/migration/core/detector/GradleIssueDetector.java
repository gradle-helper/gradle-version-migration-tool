package com.migration.core.detector;

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
        // 1. Deprecated compile/runtime configurations
        ISSUE_PATTERNS.put("DEPRECATED_CONFIGURATIONS", new IssuePattern(
            Pattern.compile("\\b(compile|runtime|testCompile|testRuntime)\\s+"),
            "CRITICAL",
            "Deprecated Configuration Usage",
            "The 'compile', 'runtime', 'testCompile', and 'testRuntime' configurations are removed in Gradle 9. " +
            "These must be replaced with 'implementation', 'runtimeOnly', 'testImplementation', and 'testRuntimeOnly' respectively.",
            true
        ));
        
        // 2. Deprecated API usage
        ISSUE_PATTERNS.put("DEPRECATED_API", new IssuePattern(
            Pattern.compile("\\bconvention\\s*\\.\\s*getPlugin|\\bconvention\\s*\\["),
            "HIGH",
            "Deprecated Convention API",
            "The Convention API is removed in Gradle 9. Use the newer Provider API and extensions instead. " +
            "Replace convention.getPlugin() with project.extensions.getByType().",
            true
        ));
        
        // 3. AbstractArchiveTask changes
        ISSUE_PATTERNS.put("ARCHIVE_NAME", new IssuePattern(
            Pattern.compile("\\barchiveName\\s*=|\\barchiveBaseName\\s*=|\\barchiveVersion\\s*=|\\barchiveExtension\\s*="),
            "HIGH",
            "Deprecated Archive Task Properties",
            "Direct property assignment for archive tasks is deprecated. Use the Property API: " +
            "archiveFileName.set(), archiveBaseName.set(), archiveVersion.set(), archiveExtension.set().",
            true
        ));
        
        // 4. Gradle wrapper version
        ISSUE_PATTERNS.put("GRADLE_VERSION", new IssuePattern(
            Pattern.compile("distributionUrl=.*gradle-(\\d+\\.\\d+)"),
            "CRITICAL",
            "Gradle Version Update Required",
            "Your project is using an older Gradle version. Gradle 9.x requires updating the wrapper to version 9.0 or higher.",
            true
        ));
        
        // 5. Deprecated task configuration
        ISSUE_PATTERNS.put("TASK_LEFTSHIFT", new IssuePattern(
            Pattern.compile("task\\s+\\w+\\s*<<"),
            "HIGH",
            "Deprecated Task Configuration (<<)",
            "The << operator for task configuration is removed. Use doLast { } instead.",
            true
        ));
        
        // 6. Deprecated dynamic properties
        ISSUE_PATTERNS.put("DYNAMIC_PROPERTIES", new IssuePattern(
            Pattern.compile("\\bproject\\.ext\\[|\\bext\\["),
            "MEDIUM",
            "Dynamic Properties Usage",
            "Dynamic properties using ext[] are discouraged. Consider using typed extensions or the Provider API for better type safety.",
            false
        ));
        
        // 7. Deprecated Gradle API methods
        ISSUE_PATTERNS.put("DEPRECATED_METHODS", new IssuePattern(
            Pattern.compile("\\b(getArchivePath|getClassesDir|getDestinationDir)\\s*\\("),
            "HIGH",
            "Deprecated Gradle API Methods",
            "Methods like getArchivePath(), getClassesDir(), and getDestinationDir() are removed. " +
            "Use archiveFile.get(), classesDirectory.get(), and destinationDirectory.get() respectively.",
            true
        ));
        
        // 8. Deprecated configurations in dependencies
        ISSUE_PATTERNS.put("DEPRECATED_DEPENDENCY_CONFIG", new IssuePattern(
            Pattern.compile("(compile|runtime|testCompile|testRuntime)\\s*\\("),
            "CRITICAL",
            "Deprecated Dependency Configuration Methods",
            "Dependency configuration methods compile(), runtime(), testCompile(), and testRuntime() are removed. " +
            "Use implementation(), runtimeOnly(), testImplementation(), and testRuntimeOnly().",
            true
        ));
        
        // 9. Deprecated buildscript classpath
        ISSUE_PATTERNS.put("BUILDSCRIPT_CLASSPATH", new IssuePattern(
            Pattern.compile("buildscript\\s*\\{[^}]*classpath\\s+['\"]"),
            "MEDIUM",
            "Legacy Buildscript Classpath",
            "Consider migrating to the plugins {} block instead of buildscript {} for plugin dependencies. " +
            "This provides better dependency resolution and version management.",
            false
        ));
        
        // 10. Deprecated SourceSet output
        ISSUE_PATTERNS.put("SOURCESET_OUTPUT", new IssuePattern(
            Pattern.compile("sourceSets\\.\\w+\\.output\\.classesDir"),
            "HIGH",
            "Deprecated SourceSet Output Property",
            "The classesDir property is removed. Use classesDirs (plural) which returns a FileCollection.",
            true
        ));
        
        // 11. Deprecated task types
        ISSUE_PATTERNS.put("DEPRECATED_TASK_TYPES", new IssuePattern(
            Pattern.compile("\\b(Upload|InstallTask)\\b"),
            "HIGH",
            "Deprecated Task Types",
            "Task types like Upload and InstallTask are removed. Use the maven-publish or ivy-publish plugins instead.",
            false
        ));
        
        // 12. Deprecated Gradle properties
        ISSUE_PATTERNS.put("DEPRECATED_PROPERTIES", new IssuePattern(
            Pattern.compile("\\b(archivesBaseName|version|group)\\s*="),
            "MEDIUM",
            "Direct Property Assignment",
            "Direct assignment to properties like archivesBaseName is deprecated. " +
            "Use base.archivesName.set() for archivesBaseName in Gradle 9.",
            true
        ));
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
        switch (issueType) {
            case "DEPRECATED_CONFIGURATIONS":
                return "Gradle 9 has removed the legacy dependency configurations. The matched code '" + matchedText + 
                       "' uses a deprecated configuration. This will cause build failures. " +
                       "Migration: compile → implementation, runtime → runtimeOnly, " +
                       "testCompile → testImplementation, testRuntime → testRuntimeOnly.";
                       
            case "DEPRECATED_API":
                return "The Convention API has been removed in Gradle 9. The code '" + matchedText + 
                       "' uses this deprecated API. Replace with the Extensions API: " +
                       "project.extensions.getByType(YourExtension.class) or use the Provider API.";
                       
            case "ARCHIVE_NAME":
                return "Archive task properties must now use the Property API. The code '" + matchedText + 
                       "' uses direct assignment which is no longer supported. " +
                       "Use .set() method instead: archiveFileName.set('name.jar')";
                       
            case "GRADLE_VERSION":
                return "Your gradle-wrapper.properties specifies an older Gradle version. " +
                       "Gradle 9.x requires updating the wrapper. Run: ./gradlew wrapper --gradle-version 9.0";
                       
            case "TASK_LEFTSHIFT":
                return "The << operator for task configuration was removed. The code '" + matchedText + 
                       "' must be updated to use doLast { } block instead.";
                       
            case "DEPRECATED_METHODS":
                return "The method in '" + matchedText + "' has been removed in Gradle 9. " +
                       "Use the Property API equivalents: getArchivePath() → archiveFile.get(), " +
                       "getClassesDir() → classesDirectory.get(), getDestinationDir() → destinationDirectory.get()";
                       
            case "DEPRECATED_DEPENDENCY_CONFIG":
                return "Dependency configuration method '" + matchedText + "' is removed in Gradle 9. " +
                       "Update to use implementation(), runtimeOnly(), testImplementation(), or testRuntimeOnly().";
                       
            case "SOURCESET_OUTPUT":
                return "The classesDir property is removed. Use classesDirs (plural) which returns a FileCollection " +
                       "containing all output directories for the source set.";
                       
            case "DEPRECATED_PROPERTIES":
                return "Direct property assignment '" + matchedText + "' should be migrated to the Property API. " +
                       "For archivesBaseName, use: base { archivesName.set('name') }";
                       
            default:
                return "This code pattern is deprecated or removed in Gradle 9 and requires migration.";
        }
    }
    
    private String generateSuggestedFix(String issueType, String matchedText) {
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
                
            case "DEPRECATED_DEPENDENCY_CONFIG":
                if (matchedText.contains("compile(")) {
                    return matchedText.replace("compile(", "implementation(");
                } else if (matchedText.contains("runtime(")) {
                    return matchedText.replace("runtime(", "runtimeOnly(");
                } else if (matchedText.contains("testCompile(")) {
                    return matchedText.replace("testCompile(", "testImplementation(");
                } else if (matchedText.contains("testRuntime(")) {
                    return matchedText.replace("testRuntime(", "testRuntimeOnly(");
                }
                break;
                
            case "ARCHIVE_NAME":
                if (matchedText.contains("archiveName =")) {
                    return matchedText.replace("archiveName =", "archiveFileName.set(");
                } else if (matchedText.contains("archiveBaseName =")) {
                    return matchedText.replace("archiveBaseName =", "archiveBaseName.set(");
                } else if (matchedText.contains("archiveVersion =")) {
                    return matchedText.replace("archiveVersion =", "archiveVersion.set(");
                }
                break;
                
            case "TASK_LEFTSHIFT":
                return matchedText.replace("<<", "{ doLast");
                
            case "DEPRECATED_METHODS":
                if (matchedText.contains("getArchivePath()")) {
                    return matchedText.replace("getArchivePath()", "archiveFile.get()");
                } else if (matchedText.contains("getClassesDir()")) {
                    return matchedText.replace("getClassesDir()", "classesDirectory.get()");
                } else if (matchedText.contains("getDestinationDir()")) {
                    return matchedText.replace("getDestinationDir()", "destinationDirectory.get()");
                }
                break;
                
            case "SOURCESET_OUTPUT":
                return matchedText.replace(".output.classesDir", ".output.classesDirs");
                
            case "DEPRECATED_PROPERTIES":
                if (matchedText.contains("archivesBaseName =")) {
                    String value = matchedText.substring(matchedText.indexOf("=") + 1).trim();
                    return "base { archivesName.set(" + value + ") }";
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
