package com.smartbilling.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Authentication filter that protects all routes except login, static
 * resources, and error pages.
 * Redirects unauthenticated users to the login page.
 */
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());

        // Allow access to public resources without authentication
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Check for valid session
        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            // User is authenticated — proceed
            chain.doFilter(request, response);
        } else {
            // Not authenticated — redirect to login
            httpResponse.sendRedirect(contextPath + "/auth/login");
        }
    }

    /**
     * Check if the requested path is a public resource that doesn't require
     * authentication.
     */
    private boolean isPublicResource(String path) {
        return path.equals("/") ||
                path.equals("") ||
                path.startsWith("/support") ||
                path.startsWith("/auth/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/error/") ||
                path.endsWith(".css") ||
                path.endsWith(".js") ||
                path.endsWith(".png") ||
                path.endsWith(".jpg") ||
                path.endsWith(".ico");
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
