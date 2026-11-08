package com.university.lab2;

import java.sql.SQLException;

/**
 * Exercise 3.2 - Our own unchecked exception for database problems.
 * DAOs catch the technical SQLException and turn it into a readable message,
 * using PostgreSQL's SQLState codes to know what went wrong.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DataAccessException from(String action, SQLException e) {
        String state = e.getSQLState() == null ? "" : e.getSQLState();
        String reason;
        if (state.equals("23505")) {
            reason = "a record with that value already exists (duplicate email / course code / enrolment)";
        } else if (state.equals("23503")) {
            reason = "it refers to a student or course that does not exist";
        } else if (state.equals("23514")) {
            reason = "a value breaks a database rule (CHECK constraint)";
        } else if (state.startsWith("08")) {
            reason = "cannot reach the database - is PostgreSQL running?";
        } else if (state.equals("28P01")) {
            reason = "wrong database username or password (see DatabaseConnection)";
        } else if (state.equals("3D000")) {
            reason = "database 'university_db' does not exist - create it first";
        } else if (state.equals("42P01")) {
            reason = "a table is missing - run sql/schema.sql first";
        } else {
            reason = e.getMessage();
        }
        return new DataAccessException("Could not " + action + ": " + reason, e);
    }
}
