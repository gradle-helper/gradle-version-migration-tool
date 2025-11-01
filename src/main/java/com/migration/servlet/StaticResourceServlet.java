package com.migration.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet(urlPatterns = {"/js/*", "/css/*", "/images/*"})
public class StaticResourceServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Set content type based on file extension
        if (path.endsWith(".css")) {
            response.setContentType("text/css");
        } else if (path.endsWith(".js")) {
            response.setContentType("application/javascript");
        } else if (path.endsWith(".png")) {
            response.setContentType("image/png");
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            response.setContentType("image/jpeg");
        } else if (path.endsWith(".gif")) {
            response.setContentType("image/gif");
        } else {
            response.setContentType("application/octet-stream");
        }
        
        // Get resource from webapp directory
        InputStream resourceStream = getServletContext().getResourceAsStream(path);
        
        if (resourceStream == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        try (OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = resourceStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            resourceStream.close();
        }
    }
}
