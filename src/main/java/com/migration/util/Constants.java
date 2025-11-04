package com.migration.util;

/**
 * Application-wide constants
 */
public final class Constants {
    
    // Prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }
    
    // Session attributes
    public static final String SESSION_PROJECT_INFO = "projectInfo";
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    
    // File patterns
    public static final String GRADLE_FILE_EXTENSION = ".gradle";
    public static final String GRADLE_KTS_FILE_EXTENSION = ".gradle.kts";
    public static final String BACKUP_FILE_SUFFIX = ".backup.";
    
    // File names
    public static final String BUILD_GRADLE = "build.gradle";
    public static final String BUILD_GRADLE_KTS = "build.gradle.kts";
    public static final String SETTINGS_GRADLE = "settings.gradle";
    public static final String SETTINGS_GRADLE_KTS = "settings.gradle.kts";
    public static final String GRADLE_WRAPPER_PROPERTIES = "gradle/wrapper/gradle-wrapper.properties";
    
    // Excluded directories
    public static final String BUILD_DIR = "/build/";
    public static final String GRADLE_DIR = "/.gradle/";
    
    // Limits
    public static final int MAX_ISSUES_PER_PATTERN_PER_FILE = 100;
    
    // API endpoints
    public static final String API_ANALYZE = "/api/analyze";
    public static final String API_FIX = "/api/fix";
    
    // HTTP headers
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CHARSET_UTF8 = "UTF-8";
    
    // Error messages
    public static final String ERROR_PROJECT_PATH_REQUIRED = "Project path is required";
    public static final String ERROR_PROJECT_PATH_INVALID = "Project path must be absolute";
    public static final String ERROR_PROJECT_NOT_FOUND = "Project directory not found";
    public static final String ERROR_NOT_GRADLE_PROJECT = "Not a valid Gradle project (missing build.gradle or settings.gradle)";
    public static final String ERROR_NO_PROJECT_IN_SESSION = "No project analysis found in session";
    public static final String ERROR_ISSUE_IDS_REQUIRED = "Issue IDs are required";
    public static final String ERROR_ANALYZING_PROJECT = "Error analyzing project: ";
    public static final String ERROR_APPLYING_FIX = "Error applying fix: ";
}
