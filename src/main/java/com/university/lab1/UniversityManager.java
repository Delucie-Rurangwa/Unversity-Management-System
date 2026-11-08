package com.university.lab1;

import java.util.*;

/**
 * Exercise 1.3 - Manages the relationships with collections:
 *   List  -> ordered collections of all students / courses
 *   Set   -> a course roster (a student can only appear once)
 *   Map   -> course -> roster, instructor -> courses taught
 */
public class UniversityManager {

    private final List<Student> students = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final Map<Course, Set<Student>> courseRosters = new HashMap<>();
    private final Map<Instructor, List<Course>> instructorAssignments = new LinkedHashMap<>();

    public boolean addStudent(Student student) {
        if (students.contains(student)) return false;   // uses equals() -> email
        return students.add(student);
    }

    public boolean addCourse(Course course) {
        if (courses.contains(course)) return false;     // uses equals() -> code
        return courses.add(course);
    }

    public void assignInstructor(Instructor instructor, Course course) {
        requireKnownCourse(course);
        // If the course already had an instructor, remove it from their list first
        Instructor previous = course.getInstructor();
        if (previous != null && instructorAssignments.containsKey(previous)) {
            instructorAssignments.get(previous).remove(course);
        }
        course.setInstructor(instructor);
        // computeIfAbsent: create the list the first time we see this instructor
        instructorAssignments.computeIfAbsent(instructor, k -> new ArrayList<>()).add(course);
    }

    /** @return false if the student was already enrolled (Set.add refuses duplicates) */
    public boolean enroll(Student student, Course course) {
        if (!students.contains(student)) throw new IllegalArgumentException("Unknown student: " + student.getName());
        requireKnownCourse(course);
        return courseRosters.computeIfAbsent(course, k -> new LinkedHashSet<>()).add(student);
    }

    public Set<Student> getRoster(Course course) {
        return Collections.unmodifiableSet(courseRosters.getOrDefault(course, Collections.emptySet()));
    }

    /** Reverse lookup: which courses is this student in? */
    public List<Course> getCoursesFor(Student student) {
        List<Course> result = new ArrayList<>();
        for (Course course : courses) {
            if (getRoster(course).contains(student)) result.add(course);
        }
        return result;
    }

    public List<Course> getCoursesTaughtBy(Instructor instructor) {
        return Collections.unmodifiableList(instructorAssignments.getOrDefault(instructor, Collections.emptyList()));
    }

    public List<Student> getStudents() { return Collections.unmodifiableList(students); }
    public List<Course> getCourses() { return Collections.unmodifiableList(courses); }
    public Set<Instructor> getInstructors() { return Collections.unmodifiableSet(instructorAssignments.keySet()); }

    /** Generic helper: works for a collection of ANY type T. */
    public static <T> void printAll(String title, Collection<T> items) {
        System.out.println("--- " + title + " (" + items.size() + ") ---");
        for (T item : items) System.out.println("  " + item);
    }

    private void requireKnownCourse(Course course) {
        if (!courses.contains(course)) throw new IllegalArgumentException("Unknown course: " + course.getCode());
    }
}
