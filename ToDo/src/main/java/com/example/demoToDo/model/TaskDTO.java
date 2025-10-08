package com.example.demoToDo.model;

public class TaskDTO {
   
    private Long id;
    private String title;
    private String description;
    private boolean completed;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
}
