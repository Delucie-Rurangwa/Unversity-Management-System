package com.university.lab2;

import com.university.lab1.Course;
import com.university.lab1.Student;
import com.university.lab1.UndergraduateStudent;

import java.util.List;

/**
 * Runs every CRUD operation once against PostgreSQL, then cleans up after itself
 * (unique suffix + delete at the end), so it can be run many times.
 */
public class Lab2Demo {

    public static void run() {
        System.out.println("\n================ LAB 2: JDBC + DAO ================");
        StudentDAO studentDAO = new StudentDAO();
        CourseDAO courseDAO = new CourseDAO();
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

        String tag = String.valueOf(System.currentTimeMillis() % 100000);
        try {
            // CREATE
            Student s = studentDAO.save(new UndergraduateStudent(0, "Demo Student", "demo" + tag + "@uni.edu", 1));
            Course c = courseDAO.save(new Course(0, "DEMO" + tag, "Demo Course", 3));
            System.out.println("Inserted -> " + s);
            System.out.println("Inserted -> " + c);

            // READ
            List<Course> courses = courseDAO.findAll();
            System.out.println("All courses in DB (" + courses.size() + "):");
            courses.forEach(course -> System.out.println("  " + course));

            // UPDATE enrolment (enroll, then set a grade) and UPDATE student
            enrollmentDAO.enroll(s.getId(), c.getId());
            System.out.println("Enrolled student " + s.getId() + " in course " + c.getCode());
            System.out.println("Grade updated? " + enrollmentDAO.updateGrade(s.getId(), c.getId(), 3.5));

            s.setName("Demo Student Renamed");
            System.out.println("Student updated? " + studentDAO.update(s));
            System.out.println("Read back -> " + studentDAO.findById(s.getId()).orElse(null));

            // SQL injection test (Exercise 2.3): the payload is treated as plain text
            String attack = "' OR '1'='1";
            System.out.println("Search with injection payload " + attack + " -> "
                    + studentDAO.searchByName(attack).size() + " rows (safe: no rows leaked)");

            // DELETE (cleanup)
            System.out.println("Student deleted? " + studentDAO.delete(s.getId()) + " (enrolment removed by CASCADE)");
            System.out.println("Course deleted?  " + courseDAO.delete(c.getId()));
        } catch (DataAccessException e) {
            System.out.println("[DB ERROR] " + e.getMessage());
        }
    }
}
