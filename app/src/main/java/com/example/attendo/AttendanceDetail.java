package com.example.attendo;// AttendanceDetail.java

public class AttendanceDetail {
    private String date;
    private String status;

    public AttendanceDetail(String date, String status) {
        this.date = date;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}