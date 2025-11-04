package com.migration.servlet;

import com.google.gson.Gson;
import com.migration.api.request.FixRequest;
import com.migration.api.response.ErrorResponse;
import com.migration.core.fixer.AutoFixer;
import com.migration.model.MigrationIssue;
import com.migration.model.ProjectInfo;
import com.migration.util.Constants;
import com.migration.util.SessionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(Constants.API_FIX)
public class IssueFixerServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final AutoFixer fixer = new AutoFixer();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType(Constants.CONTENT_TYPE_JSON);
        response.setCharacterEncoding(Constants.CHARSET_UTF8);
        
        PrintWriter out = response.getWriter();
        
        try {
            // Read request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            FixRequest fixRequest = gson.fromJson(sb.toString(), FixRequest.class);
            
            if (fixRequest == null || fixRequest.getIssueIds() == null || fixRequest.getIssueIds().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(gson.toJson(new ErrorResponse(Constants.ERROR_ISSUE_IDS_REQUIRED)));
                return;
            }
            
            // Get current project from session
            ProjectInfo projectInfo = SessionManager.getProjectInfo(request);
            if (projectInfo == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(gson.toJson(new ErrorResponse(Constants.ERROR_NO_PROJECT_IN_SESSION)));
                return;
            }
            
            // Find issues to fix
            List<MigrationIssue> issuesToFix = new ArrayList<>();
            for (String issueId : fixRequest.getIssueIds()) {
                projectInfo.getIssues().stream()
                    .filter(issue -> issue.getId().equals(issueId))
                    .findFirst()
                    .ifPresent(issuesToFix::add);
            }
            
            if (issuesToFix.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(gson.toJson(new ErrorResponse("No matching issues found")));
                return;
            }
            
            // Apply fixes
            AutoFixer.BatchFixResult result = fixer.applyMultipleFixes(issuesToFix);
            
            // Update project info - remove fixed issues
            if (result.getSuccessCount() > 0) {
                List<MigrationIssue> remainingIssues = new ArrayList<>(projectInfo.getIssues());
                for (AutoFixer.FixResult fixResult : result.getResults()) {
                    if (fixResult.isSuccess()) {
                        remainingIssues.removeIf(issue -> issue.getId().equals(fixResult.getIssueId()));
                    }
                }
                projectInfo.setIssues(remainingIssues);
                projectInfo.setTotalIssues(remainingIssues.size());
                SessionManager.storeProjectInfo(request, projectInfo);
            }
            
            response.setStatus(HttpServletResponse.SC_OK);
            out.write(gson.toJson(result));
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(gson.toJson(new ErrorResponse(Constants.ERROR_APPLYING_FIX + e.getMessage())));
        }
    }
}
