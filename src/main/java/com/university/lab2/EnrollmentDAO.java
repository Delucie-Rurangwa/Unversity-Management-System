package com.university.lab2;

import com.university.lab1.Course;
import com.university.lab1.Student;

import java.sql.*;
import java.util.*;

/**
 * Exercise 2.2 / 3.1 - The enrollments junction table, plus the JOIN query
 * that shows every student with their courses.
 */
public class EnrollmentDAO {

    private static final String ENROLL = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
    private static final String UPDATE_GRADE = "UPDATE enrollments SET grade = ? WHERE student_id = ? AND course_id = ?";
    private static final String UNENROLL = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";

    // LEFT JOIN keeps students who have no courses yet (their course columns come back NULL)
    private static final String STUDENTS_WITH_COURSES =
            "SELECT s.id AS student_id, s.name, s.email, s.student_type, " +
            "       c.id AS course_id, c.code, c.title, c.credits, e.grade " +
            "FROM students s " +
            "LEFT JOIN enrollments e ON e.student_id = s.id " +
            "LEFT JOIN courses c     ON c.id = e.course_id " +
            "ORDER BY s.id, c.code";

    public void enroll(int studentId, int courseId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(ENROLL)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw DataAccessException.from("enroll student " + studentId + " in course " + courseId, e);
        }
    }

    /** "Update enrollment": record or change the grade. False if no such enrolment. */
    public boolean updateGrade(int studentId, int courseId, double grade) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_GRADE)) {
            ps.setDouble(1, grade);
            ps.setInt(2, studentId);
            ps.setInt(3, courseId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw DataAccessException.from("update grade", e);
        }
    }

    public boolean unenroll(int studentId, int courseId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(UNENROLL)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw DataAccessException.from("drop enrolment", e);
        }
    }

    /**
     * The JOIN returns one row per (student, course) pair, so a student with 3 courses
     * appears in 3 rows. We fold those rows back into Map<Student, List<Course>>
     * and load each grade into the Student so calculateGPA() works on DB data.
     */
    public Map<Student, List<Course>> findStudentsWithCourses() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(STUDENTS_WITH_COURSES);
             ResultSet rs = ps.executeQuery()) {

            Map<Integer, Student> seen = new HashMap<>();
            Map<Student, List<Course>> result = new LinkedHashMap<>();

            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                Student student = seen.get(studentId);
                if (student == null) {   // first row for this student
                    student = StudentDAO.createStudent(studentId, rs.getString("name"),
                            rs.getString("email"), rs.getString("student_type"));
                    seen.put(studentId, student);
                    result.put(student, new ArrayList<>());
                }

                int courseId = rs.getInt("course_id");
                if (rs.wasNull()) continue;   // student with no enrolments

                Course course = new Course(courseId, rs.getString("code"),
                        rs.getString("title"), rs.getInt("credits"));
                result.get(student).add(course);

                double grade = rs.getDouble("grade");
                if (!rs.wasNull()) student.addGrade(course, grade);
            }
            return result;
        } catch (SQLException e) {
            throw DataAccessException.from("load students with courses", e);
        }
    }
}
