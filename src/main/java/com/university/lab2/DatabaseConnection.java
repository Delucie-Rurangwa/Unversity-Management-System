package com.university.lab2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * One place that knows HOW to connect. Every DAO asks this class for a connection.
 * Credentials come from environment variables if set, otherwise the defaults below.
 * >>> Change DEFAULT_PASSWORD to your own PostgreSQL password. <<<
 */
public final class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/university_db";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "postgres";

    private static final String URL = env("DB_URL", DEFAULT_URL);
    private static final String USER = env("DB_USER", DEFAULT_USER);
    private static final String PASSWORD = env("DB_PASSWORD", DEFAULT_PASSWORD);

    private DatabaseConnection() { }   // utility class: no objects

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
