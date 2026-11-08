package com.university.lab1;

import java.util.HashMap;
import java.util.Map;

public abstract class Student {

    // private fields = encapsulation (no direct access from outside)
    private int id;
    private String name;
    private String email;

    // Constructor
    public Student(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        Map<Course, Double> grades = new HashMap<>();   // NEW: course -> grade (0.0 to 4.0)

    }



    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name=" + name + ", email=" + email + "}";
    }


    }