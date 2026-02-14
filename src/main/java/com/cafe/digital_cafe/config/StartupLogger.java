package com.cafe.digital_cafe.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class StartupLogger {

    private final DataSource dataSource;
    private final Environment env;

    public StartupLogger(DataSource dataSource, Environment env) {
        this.dataSource = dataSource;
        this.env = env;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        String port = env.getProperty("server.port", "8080");
        String baseUrl = "http://localhost:" + port;
        String dbStatus = getDatabaseStatus();

        System.out.println();
        System.out.println("  ========================================================");
        System.out.println("  DIGITAL CAFE - STARTUP");
        System.out.println("  ========================================================");
        System.out.println("  Database (MySQL) : " + dbStatus);
        System.out.println("  Application     : RUNNING");
        System.out.println("  Port            : " + port);
        System.out.println("  URL             : " + baseUrl);
        System.out.println("  ========================================================");
        System.out.println();
    }

    private String getDatabaseStatus() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                String catalog = conn.getCatalog();
                return "CONNECTED (database = " + (catalog != null ? catalog : "default") + ")";
            }
            return "CONNECTION CHECK FAILED";
        } catch (Exception e) {
            return "NOT CONNECTED - " + e.getMessage();
        }
    }
}
