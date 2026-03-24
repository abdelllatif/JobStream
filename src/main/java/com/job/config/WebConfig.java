package com.job.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile-pictures/**")
                .addResourceLocations("file:./uploads/profile-pictures/")
                .setCachePeriod(3600); // Cache for 1 hour

        registry.addResourceHandler("/cvs/**")
                .addResourceLocations("file:./uploads/cvs/")
                .setCachePeriod(3600);
    }
}
