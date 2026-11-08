package com.university.lab1;

public class Course {

    // private fields = encapsulation
    private String code;
    private String title;
    private int credits;

    // Constructor
    public Course(String code, String title, int credits) {
        this.code = code;
        this.title = title;
        this.credits = credits;
    }

    // Getters
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }

    // Setters
    public void setCode(String code) { this.code = code; }
    public void setTitle(String title) { this.title = title; }
    public void setCredits(int credits) { this.credits = credits; }

    @Override
    public String toString() {
        return "Course{code=" + code + ", title=" + title + ", credits=" + credits + "}";
    }
}