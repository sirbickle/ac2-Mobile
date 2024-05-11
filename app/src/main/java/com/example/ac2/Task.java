package com.example.ac2;

public class Task {

    private String title;
    private String description;
    private String priority;
    private String value;
    private String date;
    private boolean isCompleted;

    public Task(String title, String description, String value, String date, String priority, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.value = value;
        this.date = date;
        this.priority = priority;
        this.isCompleted = isCompleted;
    }

    public Task() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
