package com.smartbilling.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Root application configuration.
 * Scans for service, dao, and utility components.
 */
@Configuration
@ComponentScan(basePackages = {
    "com.smartbilling.dao",
    "com.smartbilling.service",
    "com.smartbilling.util"
})
public class AppConfig {
    // Root context configuration
    // Service and DAO beans are auto-discovered via @ComponentScan
}
