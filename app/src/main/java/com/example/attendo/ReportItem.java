// ReportItem.java
package com.example.attendo;

public class ReportItem {

    private String name;
    private double percentage;
    private String status;
    private String branch;
    private int semester;
    private long studentId;

    public ReportItem(String name, double percentage, String status, String branch, int semester, long studentId) {
        this.name = name;
        this.percentage = percentage;
        this.status = status;
        this.branch = branch;
        this.semester = semester;
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getStatus() {
        return status;
    }

    public String getBranch() {
        return branch;
    }

    public int getSemester() {
        return semester;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return "ReportItem{" +
                "name='" + name + '\'' +
                ", percentage=" + percentage +
                ", status='" + status + '\'' +
                ", branch='" + branch + '\'' +
                ", semester=" + semester +
                ", studentId=" + studentId +
                '}';
    }
}