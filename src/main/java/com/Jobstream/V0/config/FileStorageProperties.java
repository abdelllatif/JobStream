package com.Jobstream.V0.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.file")
@Getter
@Setter
public class FileStorageProperties {

    private String storagePath;
    private String baseUrl;
}
