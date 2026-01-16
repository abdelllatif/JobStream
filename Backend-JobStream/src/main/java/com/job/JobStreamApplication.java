package com.job;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class JobStreamApplication {

    public static void main(String[] args) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            // Set system properties from .env file
            if (dotenv.get("POSTGRES_DB") != null) {
                System.setProperty("POSTGRES_DB", dotenv.get("POSTGRES_DB"));
            }
            if (dotenv.get("POSTGRES_USER") != null) {
                System.setProperty("POSTGRES_USER", dotenv.get("POSTGRES_USER"));
            }
            if (dotenv.get("POSTGRES_PASSWORD") != null) {
                System.setProperty("POSTGRES_PASSWORD", dotenv.get("POSTGRES_PASSWORD"));
            }
            if (dotenv.get("POSTGRES_PORT") != null) {
                System.setProperty("POSTGRES_PORT", dotenv.get("POSTGRES_PORT"));
            }
            if (dotenv.get("DB_URL") != null) {
                System.setProperty("DB_URL", dotenv.get("DB_URL"));
            }
            if (dotenv.get("DB_USER") != null) {
                System.setProperty("DB_USER", dotenv.get("DB_USER"));
            }
            if (dotenv.get("DB_PASSWORD") != null) {
                System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
            }
            if (dotenv.get("JWT_SECRET") != null) {
                System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
            }
            if (dotenv.get("LIQUIBASE_CHANGELOG") != null) {
                System.setProperty("LIQUIBASE_CHANGELOG", dotenv.get("LIQUIBASE_CHANGELOG"));
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load .env file: " + e.getMessage());
        }

        SpringApplication.run(JobStreamApplication.class, args);
    }
}
