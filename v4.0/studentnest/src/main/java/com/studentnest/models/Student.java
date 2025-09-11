package com.studentnest.models;

public class Student extends User {
    private String studentId;

    public Student() {
        super();
    }

    public Student(String name, String phone, String username, String password, String studentId) {
        super(name, phone, username, password, "Student");
        this.studentId = studentId;
    }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}