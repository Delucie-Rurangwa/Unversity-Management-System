# University Management System - Java BE Phase 1 Capstone

Java 17 + JDBC + PostgreSQL console app built in three labs.

| Package | Lab | Contents |
|---|---|---|
| `com.university.lab1` | OOP & Collections | Person, Student, UndergraduateStudent, GraduateStudent, Instructor, Course, UniversityManager, Lab1Demo |
| `com.university.lab2` | JDBC & DAO | DatabaseConnection, DataAccessException, GenericDAO, StudentDAO, CourseDAO, EnrollmentDAO, Lab2Demo |
| `com.university.lab3` | Console app | InputHelper, UniversityConsoleApp |
| `com.university` | Entry point | Main |

## Setup
1. In pgAdmin: `CREATE DATABASE university_db;`
2. Open the Query Tool on `university_db` and run `sql/schema.sql`.
3. Set your password in `lab2/DatabaseConnection.java` (or env vars `DB_URL`, `DB_USER`, `DB_PASSWORD`).
4. Open the folder in IntelliJ as a Maven project, then run `Main`.

## Features
- Add / remove / list students and courses
- Enroll, grade and drop enrolments
- View students with their courses (LEFT JOIN)
- Polymorphic GPA (graduate grades below 2.0 count as 0)
- PreparedStatements everywhere; friendly errors for invalid input and DB failures
