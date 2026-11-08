package com.university.lab1;

import com.university.lab1.Student;

public class GraduateStudent extends Student {

    private String researchArea;

    public GraduateStudent(int id, String name, String email, String researchArea) {
        super(id, name, email);
        this.researchArea = researchArea;
    }

    @Override
    public double calculateGPA() {
        double totalPoints = 0;
        int totalCredits = 0;

        for (Course course : getGrades().keySet()) {
            double grade = getGrades().get(course);
            if (grade < 2.0) {
                grade = 0.0;   // THE DIFFERENCE: weak grades don't count for graduates
            }
            totalPoints += grade * course.getCredits();
            totalCredits += course.getCredits();
        }

        if (totalCredits == 0) return 0.0;
        return totalPoints / totalCredits;
    }

    public String getResearchArea() { return researchArea; }
    public void setResearchArea(String researchArea) { this.researchArea = researchArea; }

    @Override
    public String toString() {
        return "Graduate " + super.toString() + " research=" + researchArea;
    }
}