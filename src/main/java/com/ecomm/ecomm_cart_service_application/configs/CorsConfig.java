package com.ecomm.ecomm_cart_service_application.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow requests to all endpoints
                .allowedOrigins("http://localhost:3000") // Explicitly set the allowed origin(s) here
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // Specify valid HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow credentials (cookies, authorization headers, etc.)
    }
}