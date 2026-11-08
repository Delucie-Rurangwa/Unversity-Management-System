package com.university.lab2;

import com.university.lab1.GraduateStudent;
import com.university.lab1.Student;
import com.university.lab1.UndergraduateStudent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Exercise 2.2 / 2.3 / 2.4 - All SQL for the students table lives here.
 * Every query uses PreparedStatement with ? placeholders, so user input is
 * sent as DATA and can never be executed as SQL (prevents SQL injection).
 * try-with-resources closes Connection / Statement / ResultSet automatically.
 */
public class StudentDAO implements GenericDAO<Student, Integer> {

    private static final String INSERT = "INSERT INTO students (name, email, student_type) VALUES (?, ?, ?)";
    private static final String FIND_BY_ID = "SELECT id, name, email, student_type FROM students WHERE id = ?";
    private static final String FIND_ALL = "SELECT id, name, email, student_type FROM students ORDER BY id";
    private static final String SEARCH = "SELECT id, name, email, student_type FROM students WHERE name ILIKE ? ORDER BY id";
    private static final String UPDATE = "UPDATE students SET name = ?, email = ?, student_type = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM students WHERE id = ?";

    @Override
    public Student save(Student student) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, new String[]{"id"})) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getStudentType());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {   // the SERIAL id PostgreSQL created
                if (keys.next()) student.setId(keys.getInt(1));
            }
            return student;
        } catch (SQLException e) {
            throw DataAccessException.from("save student", e);
        }
    }

    @Override
    public Optional<Student> findById(Integer id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw DataAccessException.from("find student " + id, e);
        }
    }

    @Override
    public List<Student> findAll() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Student> students = new ArrayList<>();
            while (rs.next()) students.add(mapRow(rs));
            return students;
        } catch (SQLException e) {
            throw DataAccessException.from("list students", e);
        }
    }

    /** Partial, case-insensitive name search - good place to prove injection safety. */
    public List<Student> searchByName(String text) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH)) {
            ps.setString(1, "%" + text + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> students = new ArrayList<>();
                while (rs.next()) students.add(mapRow(rs));
                return students;
            }
        } catch (SQLException e) {
            throw DataAccessException.from("search students", e);
        }
    }

    @Override
    public boolean update(Student student) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getStudentType());
            ps.setInt(4, student.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw DataAccessException.from("update student", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;   // enrolments go too (ON DELETE CASCADE)
        } catch (SQLException e) {
            throw DataAccessException.from("delete student " + id, e);
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return createStudent(rs.getInt("id"), rs.getString("name"),
                rs.getString("email"), rs.getString("student_type"));
    }

    /**
     * Factory: the student_type column decides which subclass to build,
     * so polymorphism still works for objects that come from the database.
     */
    public static Student createStudent(int id, String name, String email, String type) {
        if ("GRADUATE".equalsIgnoreCase(type)) {
            return new GraduateStudent(id, name, email, null);
        }
        return new UndergraduateStudent(id, name, email, 1);
    }
}
