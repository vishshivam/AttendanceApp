// Student.java
package com.example.attendo;

public class ManageStudent {
    private long id;
    private String name;
    private String branch;
    private int semester;

    public ManageStudent(long id, String name, String branch, int semester) {
        this.id = id;
        this.name = name;
        this.branch = branch;
        this.semester = semester;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }

    public int getSemester() {
        return semester;
    }
}