package com.university.lab3;

import com.university.lab1.Course;
import com.university.lab1.GraduateStudent;
import com.university.lab1.Student;
import com.university.lab1.UndergraduateStudent;
import com.university.lab2.CourseDAO;
import com.university.lab2.DataAccessException;
import com.university.lab2.EnrollmentDAO;
import com.university.lab2.StudentDAO;

import java.util.List;
import java.util.Map;

/**
 * Exercise 3.1 / 3.2 - Console CRUD app combining Lab 1 (models) and Lab 2 (DAOs).
 * This class only handles the conversation with the user; all SQL stays in the DAOs.
 */
public class UniversityConsoleApp {

    private final InputHelper in;
    private final StudentDAO studentDAO = new StudentDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public UniversityConsoleApp(InputHelper in) {
        this.in = in;
    }

    public void run() {
        System.out.println("\n================ LAB 3: UNIVERSITY CONSOLE APP ================");
        while (true) {
            printMenu();
            int choice = in.readInt("Choose an option: ", 0, 11);
            if (choice == 0) {
                System.out.println("Leaving the console app.");
                return;
            }
            // One try/catch protects every action: an error prints a message
            // and the menu comes back instead of the program crashing.
            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> removeStudent();
                    case 3 -> listStudents();
                    case 4 -> addCourse();
                    case 5 -> removeCourse();
                    case 6 -> listCourses();
                    case 7 -> enroll();
                    case 8 -> recordGrade();
                    case 9 -> dropEnrollment();
                    case 10 -> viewStudentsWithCourses();
                    case 11 -> showGpa();
                    default -> System.out.println("Unknown option.");
                }
            } catch (DataAccessException e) {
                System.out.println("[Database error] " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("[Invalid input] " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("""

                ----- MENU -----
                 1. Add student            7. Enroll student in course
                 2. Remove student         8. Record / update grade
                 3. List students          9. Drop enrolment
                 4. Add course            10. Students with their courses (JOIN)
                 5. Remove course         11. Show a student's GPA
                 6. List courses           0. Back""");
    }

    private void addStudent() {
        String name = in.readNonBlank("Full name: ");
        String email = in.readEmail("Email: ");
        int type = in.readInt("Type (1 = Undergraduate, 2 = Graduate): ", 1, 2);
        Student student = (type == 1)
                ? new UndergraduateStudent(0, name, email, 1)
                : new GraduateStudent(0, name, email, null);
        studentDAO.save(student);
        System.out.println("Saved: " + student);
    }

    private void removeStudent() {
        if (!listStudents()) return;
        int id = in.readInt("ID of student to remove: ", 1, Integer.MAX_VALUE);
        if (!in.confirm("This also removes their enrolments. Continue?")) return;
        System.out.println(studentDAO.delete(id) ? "Student removed." : "No student with id " + id + ".");
    }

    /** @return false if there is nothing to show (so callers can stop early) */
    private boolean listStudents() {
        List<Student> students = studentDAO.findAll();
        if (students.isEmpty()) {
            System.out.println("No students yet.");
            return false;
        }
        System.out.printf("%-4s %-25s %-28s %s%n", "ID", "Name", "Email", "Type");
        for (Student s : students) {
            System.out.printf("%-4d %-25s %-28s %s%n", s.getId(), s.getName(), s.getEmail(), s.getStudentType());
        }
        return true;
    }

    private void addCourse() {
        String code = in.readNonBlank("Course code (max 10 chars, e.g. CS301): ");
        if (code.length() > 10) throw new IllegalArgumentException("Course code is longer than 10 characters");
        String title = in.readNonBlank("Title: ");
        int credits = in.readInt("Credits (1-10): ", 1, 10);
        Course course = courseDAO.save(new Course(0, code, title, credits));
        System.out.println("Saved: " + course);
    }

    private void removeCourse() {
        if (!listCourses()) return;
        int id = in.readInt("ID of course to remove: ", 1, Integer.MAX_VALUE);
        if (!in.confirm("This also removes its enrolments. Continue?")) return;
        System.out.println(courseDAO.delete(id) ? "Course removed." : "No course with id " + id + ".");
    }

    private boolean listCourses() {
        List<Course> courses = courseDAO.findAll();
        if (courses.isEmpty()) {
            System.out.println("No courses yet.");
            return false;
        }
        System.out.printf("%-4s %-8s %-30s %s%n", "ID", "Code", "Title", "Credits");
        for (Course c : courses) {
            System.out.printf("%-4d %-8s %-30s %d%n", c.getId(), c.getCode(), c.getTitle(), c.getCredits());
        }
        return true;
    }

    private void enroll() {
        if (!listStudents()) return;
        int studentId = in.readInt("Student ID: ", 1, Integer.MAX_VALUE);
        if (!listCourses()) return;
        int courseId = in.readInt("Course ID: ", 1, Integer.MAX_VALUE);
        enrollmentDAO.enroll(studentId, courseId);   // DB rejects bad ids / duplicates
        System.out.println("Enrolled.");
    }

    private void recordGrade() {
        int studentId = in.readInt("Student ID: ", 1, Integer.MAX_VALUE);
        int courseId = in.readInt("Course ID: ", 1, Integer.MAX_VALUE);
        double grade = in.readDouble("Grade points (0.0 - 4.0): ", 0.0, 4.0);
        boolean updated = enrollmentDAO.updateGrade(studentId, courseId, grade);
        System.out.println(updated ? "Grade saved." : "That student is not enrolled in that course.");
    }

    private void dropEnrollment() {
        int studentId = in.readInt("Student ID: ", 1, Integer.MAX_VALUE);
        int courseId = in.readInt("Course ID: ", 1, Integer.MAX_VALUE);
        System.out.println(enrollmentDAO.unenroll(studentId, courseId) ? "Enrolment dropped." : "No such enrolment.");
    }

    private void viewStudentsWithCourses() {
        Map<Student, List<Course>> data = enrollmentDAO.findStudentsWithCourses();
        if (data.isEmpty()) {
            System.out.println("No students yet.");
            return;
        }
        for (Map.Entry<Student, List<Course>> entry : data.entrySet()) {
            Student s = entry.getKey();
            System.out.println(s.getId() + ". " + s.getName() + " (" + s.getStudentType() + ")");
            if (entry.getValue().isEmpty()) {
                System.out.println("     - not enrolled in any course");
            }
            for (Course c : entry.getValue()) {
                Double grade = s.getGrades().get(c);
                System.out.println("     - " + c.getCode() + " " + c.getTitle()
                        + (grade == null ? " (no grade yet)" : " grade " + grade));
            }
        }
    }

    private void showGpa() {
        int studentId = in.readInt("Student ID: ", 1, Integer.MAX_VALUE);
        for (Student s : enrollmentDAO.findStudentsWithCourses().keySet()) {
            if (s.getId() == studentId) {
                // Polymorphism with database data: the right GPA rule is chosen automatically
                System.out.printf("%s (%s) GPA = %.2f from %d graded course(s)%n",
                        s.getName(), s.getStudentType(), s.calculateGPA(), s.getGrades().size());
                return;
            }
        }
        System.out.println("No student with id " + studentId + ".");
    }
}
