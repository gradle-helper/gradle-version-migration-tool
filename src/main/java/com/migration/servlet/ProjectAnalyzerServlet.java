package com.migration.servlet;

import com.google.gson.Gson;
import com.migration.detector.GradleIssueDetector;
import com.migration.model.ProjectInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/analyze")
public class ProjectAnalyzerServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final GradleIssueDetector detector = new GradleIssueDetector();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        try {
            // Get project path from request
            String projectPath = request.getParameter("projectPath");
            
            if (projectPath == null || projectPath.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(new ErrorResponse("Project path is required")));
                return;
            }
            
            // Analyze project
            ProjectInfo projectInfo = detector.analyzeProject(projectPath);
            
            // Store in session for later use
            HttpSession session = request.getSession();
            session.setAttribute("currentProject", projectInfo);
            
            // Return analysis results
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(gson.toJson(projectInfo));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(gson.toJson(new ErrorResponse("Error analyzing project: " + e.getMessage())));
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            ProjectInfo projectInfo = (ProjectInfo) session.getAttribute("currentProject");
            if (projectInfo != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write(gson.toJson(projectInfo));
                return;
            }
        }
        
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        out.write(gson.toJson(new ErrorResponse("No project analysis found in session")));
    }
    
    private static class ErrorResponse {
        private final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
    }
}
