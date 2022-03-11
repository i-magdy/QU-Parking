package com.example.quparking.model;

public class Report {

    private String name;
    private String email;
    private String phone;
    private String feedback;

    public Report() {
    }

    public Report(String name, String email, String phone, String feedback) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.feedback = feedback;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getFeedback() {
        return feedback;
    }
}
