package com.gk.study.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${File.uploadPath}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        String location = "file:" + uploadPath;
        if (!location.endsWith("/") && !location.endsWith("\\")) {
            location = location + "/";
        }
        registry.addResourceHandler("/staticfiles/**").addResourceLocations(location);
    }
}
