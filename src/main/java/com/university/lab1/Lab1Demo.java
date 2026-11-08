package com.university.lab1;

/** Runs Lab 1 entirely in memory - no database needed. */
public class Lab1Demo {

    public static void run() {
        System.out.println("\n================ LAB 1: OOP + COLLECTIONS ================");

        // ---- Exercise 1.1: create objects ----
        Instructor alice = new Instructor(1, "Dr. Alice Mukamana", "alice@uni.edu", "Computer Science");
        Instructor eric  = new Instructor(2, "Dr. Eric Habimana",  "eric@uni.edu",  "Data Science");

        Course java = new Course(1, "CS101", "Introduction to Java", 4);
        Course db   = new Course(2, "CS201", "Databases", 3);
        Course ml   = new Course(3, "CS501", "Machine Learning", 4);

        Student jean  = new UndergraduateStudent(1, "Jean Uwimana", "jean@uni.edu", 2);
        Student grace = new GraduateStudent(2, "Grace Ishimwe", "grace@uni.edu", "NLP");

        // ---- Exercise 1.3: manage relationships with collections ----
        UniversityManager manager = new UniversityManager();
        manager.addStudent(jean);
        manager.addStudent(grace);
        System.out.println("Add Jean a second time? " + manager.addStudent(jean) + " (List guarded by equals)");

        manager.addCourse(java);
        manager.addCourse(db);
        manager.addCourse(ml);

        manager.assignInstructor(alice, java);
        manager.assignInstructor(alice, db);
        manager.assignInstructor(eric, ml);

        manager.enroll(jean, java);
        manager.enroll(jean, db);
        manager.enroll(grace, java);
        manager.enroll(grace, db);
        manager.enroll(grace, ml);
        System.out.println("Enroll Jean in CS101 again? " + manager.enroll(jean, java) + " (Set rejects duplicates)");

        // Same grades for both students on purpose, to show the GPA rules differ
        jean.addGrade(java, 3.7);
        jean.addGrade(db, 1.7);
        grace.addGrade(java, 3.7);
        grace.addGrade(db, 1.7);

        UniversityManager.printAll("Students", manager.getStudents());
        UniversityManager.printAll("Courses", manager.getCourses());

        // ---- Exercise 1.2: polymorphism ----
        System.out.println("--- Polymorphism: same call, different rule ---");
        for (Student s : manager.getStudents()) {   // declared type Student, real object picks the rule
            System.out.printf("  %-15s %-14s GPA = %.2f%n", s.getName(), s.getStudentType(), s.calculateGPA());
        }

        System.out.println("--- Course rosters (Map<Course, Set<Student>>) ---");
        for (Course c : manager.getCourses()) {
            System.out.print("  " + c.getCode() + ": ");
            manager.getRoster(c).forEach(s -> System.out.print(s.getName() + "; "));
            System.out.println();
        }

        System.out.println("--- Instructor assignments (Map<Instructor, List<Course>>) ---");
        for (Instructor i : manager.getInstructors()) {
            System.out.print("  " + i.getName() + " teaches: ");
            manager.getCoursesTaughtBy(i).forEach(c -> System.out.print(c.getCode() + " "));
            System.out.println();
        }

        System.out.print("--- Jean's courses (reverse lookup): ");
        manager.getCoursesFor(jean).forEach(c -> System.out.print(c.getCode() + " "));
        System.out.println();
    }
}
