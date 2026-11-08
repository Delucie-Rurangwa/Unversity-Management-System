package com.university.lab1;

import com.university.lab1.Course;
import com.university.lab1.Student;

import java.util.Map;

public class UndergraduateStudent extends Student {

    private int yearOfStudy;

    public UndergraduateStudent(int id, String name, String email, int yearOfStudy) {
        super(id, name, email);   // let Student set id, name and email
        this.yearOfStudy = yearOfStudy;
    }

    @Override
    public double calculateGPA() {
        double totalPoints = 0;
        int totalCredits = 0;

        for (Course course : getGrades().keySet()) {
            double grade = getGrades().get(course);
            totalPoints += grade * course.getCredits();
            totalCredits += course.getCredits();
        }

        if (totalCredits == 0) return 0.0;   // no grades yet: avoid dividing by zero
        return totalPoints / totalCredits;
    }



    public int getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    @Override
    public String toString() {
        return "Undergraduate " + super.toString() + " year=" + yearOfStudy;
    }
}