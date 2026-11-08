package com.university.lab2;

import com.university.lab1.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Exercise 2.2 / 2.4 - All SQL for the courses table. Same structure as StudentDAO. */
public class CourseDAO implements GenericDAO<Course, Integer> {

    private static final String INSERT = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
    private static final String FIND_BY_ID = "SELECT id, code, title, credits FROM courses WHERE id = ?";
    private static final String FIND_ALL = "SELECT id, code, title, credits FROM courses ORDER BY code";
    private static final String UPDATE = "UPDATE courses SET code = ?, title = ?, credits = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM courses WHERE id = ?";

    @Override
    public Course save(Course course) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT, new String[]{"id"})) {
            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) course.setId(keys.getInt(1));
            }
            return course;
        } catch (SQLException e) {
            throw DataAccessException.from("save course", e);
        }
    }

    @Override
    public Optional<Course> findById(Integer id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw DataAccessException.from("find course " + id, e);
        }
    }

    @Override
    public List<Course> findAll() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            List<Course> courses = new ArrayList<>();
            while (rs.next()) courses.add(mapRow(rs));
            return courses;
        } catch (SQLException e) {
            throw DataAccessException.from("list courses", e);
        }
    }

    @Override
    public boolean update(Course course) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {
            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());
            ps.setInt(4, course.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw DataAccessException.from("update course", e);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw DataAccessException.from("delete course " + id, e);
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        return new Course(rs.getInt("id"), rs.getString("code"), rs.getString("title"), rs.getInt("credits"));
    }
}
