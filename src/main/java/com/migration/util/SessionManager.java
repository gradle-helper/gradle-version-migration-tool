package com.migration.util;

import com.migration.model.ProjectInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility class for managing HTTP session operations
 */
public final class SessionManager {
    
    // Prevent instantiation
    private SessionManager() {
        throw new AssertionError("Cannot instantiate SessionManager class");
    }
    
    /**
     * Stores project information in the session
     * 
     * @param request the HTTP request
     * @param projectInfo the project information to store
     */
    public static void storeProjectInfo(HttpServletRequest request, ProjectInfo projectInfo) {
        HttpSession session = request.getSession(true);
        session.setAttribute(Constants.SESSION_PROJECT_INFO, projectInfo);
    }
    
    /**
     * Retrieves project information from the session
     * 
     * @param request the HTTP request
     * @return the project information, or null if not found
     */
    public static ProjectInfo getProjectInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (ProjectInfo) session.getAttribute(Constants.SESSION_PROJECT_INFO);
    }
    
    /**
     * Checks if project information exists in the session
     * 
     * @param request the HTTP request
     * @return true if project info exists, false otherwise
     */
    public static boolean hasProjectInfo(HttpServletRequest request) {
        return getProjectInfo(request) != null;
    }
    
    /**
     * Clears project information from the session
     * 
     * @param request the HTTP request
     */
    public static void clearProjectInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(Constants.SESSION_PROJECT_INFO);
        }
    }
    
    /**
     * Invalidates the entire session
     * 
     * @param request the HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
