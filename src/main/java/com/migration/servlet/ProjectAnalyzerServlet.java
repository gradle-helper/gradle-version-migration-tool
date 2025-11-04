package com.migration.servlet;

import com.google.gson.Gson;
import com.migration.api.response.ErrorResponse;
import com.migration.core.detector.GradleIssueDetector;
import com.migration.model.ProjectInfo;
import com.migration.util.Constants;
import com.migration.util.PathValidator;
import com.migration.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(Constants.API_ANALYZE)
public class ProjectAnalyzerServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final GradleIssueDetector detector = new GradleIssueDetector();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType(Constants.CONTENT_TYPE_JSON);
        response.setCharacterEncoding(Constants.CHARSET_UTF8);
        
        PrintWriter out = response.getWriter();
        
        try {
            // Get and validate project path
            String projectPath = request.getParameter("projectPath");
            
            PathValidator.ValidationResult validation = PathValidator.validate(projectPath);
            if (!validation.isValid()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(new ErrorResponse(validation.getErrorMessage())));
                return;
            }
            
            // Analyze project
            ProjectInfo projectInfo = detector.analyzeProject(projectPath);
            
            // Store in session for later use
            SessionManager.storeProjectInfo(request, projectInfo);
            
            // Return analysis results
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(gson.toJson(projectInfo));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(gson.toJson(new ErrorResponse(Constants.ERROR_ANALYZING_PROJECT + e.getMessage())));
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType(Constants.CONTENT_TYPE_JSON);
        response.setCharacterEncoding(Constants.CHARSET_UTF8);
        
        PrintWriter out = response.getWriter();
        
        ProjectInfo projectInfo = SessionManager.getProjectInfo(request);
        if (projectInfo != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(gson.toJson(projectInfo));
            return;
        }
        
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        out.write(gson.toJson(new ErrorResponse(Constants.ERROR_NO_PROJECT_IN_SESSION)));
    }
}
