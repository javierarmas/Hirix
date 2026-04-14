package com.hirix.jobalerts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class JobAlertsApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

        // SENDGRID
        System.setProperty("SENDGRID_API_KEY", dotenv.get("SENDGRID_API_KEY"));
        System.setProperty("SENDGRID_FROM_EMAIL", dotenv.get("SENDGRID_FROM_EMAIL"));
        System.setProperty("ALERT_TO_EMAIL", dotenv.get("ALERT_TO_EMAIL"));

        // DB
        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        // ADZUNA
        System.setProperty("ADZUNA_APP_ID", dotenv.get("ADZUNA_APP_ID"));
        System.setProperty("ADZUNA_APP_KEY", dotenv.get("ADZUNA_APP_KEY"));
        System.setProperty("ADZUNA_COUNTRY", dotenv.get("ADZUNA_COUNTRY"));

        SpringApplication.run(JobAlertsApplication.class, args);

    }
}
