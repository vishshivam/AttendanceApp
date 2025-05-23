package com.example.attendo;

public class Student {
    private int id;
    private String name;
    private boolean isPresent;

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
        this.isPresent = false;
    }
    public Student(int id, String name, boolean isPresent) {
        this.id = id;
        this.name = name;
        this.isPresent = isPresent;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        this.isPresent = present;
    }
}