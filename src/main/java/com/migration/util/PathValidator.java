package com.migration.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for validating project paths
 */
public final class PathValidator {
    
    // Prevent instantiation
    private PathValidator() {
        throw new AssertionError("Cannot instantiate PathValidator class");
    }
    
    /**
     * Validates that the given path is a valid Gradle project directory
     * 
     * @param projectPath the path to validate
     * @return ValidationResult containing validation status and error message
     */
    public static ValidationResult validate(String projectPath) {
        // Check if path is provided
        if (projectPath == null || projectPath.trim().isEmpty()) {
            return ValidationResult.error(Constants.ERROR_PROJECT_PATH_REQUIRED);
        }
        
        // Check if path is absolute
        Path path = Paths.get(projectPath);
        if (!path.isAbsolute()) {
            return ValidationResult.error(Constants.ERROR_PROJECT_PATH_INVALID);
        }
        
        // Check if directory exists
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return ValidationResult.error(Constants.ERROR_PROJECT_NOT_FOUND);
        }
        
        // Check if it's a Gradle project
        if (!isGradleProject(path)) {
            return ValidationResult.error(Constants.ERROR_NOT_GRADLE_PROJECT);
        }
        
        return ValidationResult.success();
    }
    
    /**
     * Checks if the directory contains Gradle build files
     */
    private static boolean isGradleProject(Path directory) {
        Path buildGradle = directory.resolve(Constants.BUILD_GRADLE);
        Path buildGradleKts = directory.resolve(Constants.BUILD_GRADLE_KTS);
        Path settingsGradle = directory.resolve(Constants.SETTINGS_GRADLE);
        Path settingsGradleKts = directory.resolve(Constants.SETTINGS_GRADLE_KTS);
        
        return Files.exists(buildGradle) || 
               Files.exists(buildGradleKts) ||
               Files.exists(settingsGradle) ||
               Files.exists(settingsGradleKts);
    }
    
    /**
     * Result of path validation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
