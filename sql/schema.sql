-- Exercise 2.1 - University schema
-- Run once in pgAdmin (Query Tool) or: psql -U postgres -d university_db -f sql/schema.sql
-- (Create the database first: CREATE DATABASE university_db;)

CREATE TABLE IF NOT EXISTS students (
    id           SERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    student_type VARCHAR(20)  NOT NULL CHECK (student_type IN ('UNDERGRADUATE', 'GRADUATE')),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS courses (
    id      SERIAL PRIMARY KEY,
    code    VARCHAR(10)  NOT NULL UNIQUE,
    title   VARCHAR(150) NOT NULL,
    credits INT          NOT NULL CHECK (credits BETWEEN 1 AND 10)
);

-- Junction table: one row = one student enrolled in one course (many-to-many)
CREATE TABLE IF NOT EXISTS enrollments (
    id          SERIAL PRIMARY KEY,
    student_id  INT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    course_id   INT NOT NULL REFERENCES courses(id)  ON DELETE CASCADE,
    grade       NUMERIC(3,2) CHECK (grade BETWEEN 0 AND 4),   -- NULL until graded
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, course_id)                            -- no double enrolment
);

-- Optional seed data (safe to run twice)
INSERT INTO students (name, email, student_type) VALUES
    ('Jean Uwimana',  'jean@uni.edu',  'UNDERGRADUATE'),
    ('Grace Ishimwe', 'grace@uni.edu', 'GRADUATE')
ON CONFLICT (email) DO NOTHING;

INSERT INTO courses (code, title, credits) VALUES
    ('CS101', 'Introduction to Java', 4),
    ('CS201', 'Databases',            3),
    ('CS501', 'Machine Learning',     4)
ON CONFLICT (code) DO NOTHING;
