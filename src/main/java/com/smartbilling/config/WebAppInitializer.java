package com.smartbilling.config;

import com.smartbilling.filter.AuthenticationFilter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Web application initializer replacing web.xml for Spring MVC setup.
 * Registers the DispatcherServlet and AuthenticationFilter programmatically.
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /**
     * Register the authentication filter to protect all routes
     * except login, static resources, and error pages.
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        // Register Authentication Filter
        FilterRegistration.Dynamic authFilter = servletContext.addFilter(
                "authenticationFilter", new AuthenticationFilter());
        authFilter.addMappingForUrlPatterns(null, false, "/*");
    }
}
