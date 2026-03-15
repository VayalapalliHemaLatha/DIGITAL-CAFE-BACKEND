package com.cafe.digital_cafe.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Creates the MySQL database if it does not exist, then provides the DataSource.
 * Tables are created by JPA/Hibernate (spring.jpa.hibernate.ddl-auto=update).
 */
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class DatabaseAutoCreateConfig {

    @Primary
    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password) throws Exception {
        ensureDatabaseExists(url, username, password);
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    private void ensureDatabaseExists(String url, String username, String password) {
        int lastSlash = url.lastIndexOf('/');
        if (lastSlash <= 0) return;
        String serverUrl = url.substring(0, lastSlash + 1);
        String rest = url.substring(lastSlash + 1);
        int q = rest.indexOf('?');
        String dbName = q >= 0 ? rest.substring(0, q).trim() : rest.trim();
        if (dbName.isEmpty()) return;
        String params = q >= 0 ? url.substring(url.indexOf('?')) : "";
        String connectUrl = serverUrl + "mysql" + params;
        try (Connection c = DriverManager.getConnection(connectUrl, username, password);
             Statement s = c.createStatement()) {
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName.replace("`", "``") + "`");
            System.out.println("  Database '" + dbName + "' ensured (created if missing). Tables created/updated by JPA.");
        } catch (Exception e) {
            System.err.println("  Could not create database (ensure MySQL is running and user has permission): " + e.getMessage());
        }
    }
}
