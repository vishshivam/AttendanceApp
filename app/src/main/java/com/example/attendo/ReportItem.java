package com.example.attendo;

public class ReportItem {
    private String name;
    private double percentage;

    public ReportItem(String name, double percentage) {
        this.name = name;
        this.percentage = percentage;
    }

    public String getName() {
        return name;
    }

    public double getPercentage() {
        return percentage;
    }
}