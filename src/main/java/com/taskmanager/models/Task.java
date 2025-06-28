package com.taskmanager.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Classe che rappresenta un singolo Task.
 * È la "foglia" nel pattern Composite.
 */
public class Task implements TaskComponent {
    private final String id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Costruttore per creare un nuovo task
     * @param title titolo del task (obbligatorio)
     * @param description descrizione del task (può essere null)
     */
    public Task(String title, String description) {
        this.id = UUID.randomUUID().toString().substring(0, 8); // ID più corto per semplicità
        this.title = Objects.requireNonNull(title, "Il titolo non può essere null");
        this.description = description;
        this.status = TaskStatus.TODO;
        this.priority = TaskPriority.MEDIUM;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Implementazione di TaskComponent (per Composite Pattern)
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Il titolo non può essere null");
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public void display(int indentLevel) {
        String indent = "  ".repeat(indentLevel);
        System.out.printf("%s📋 Task: %s [%s] [%s]%n", 
                         indent, title, status, priority);
        if (description != null && !description.isEmpty()) {
            System.out.printf("%s   Descrizione: %s%n", indent, description);
        }
    }
    
    @Override
    public boolean isLeaf() {
        return true; // I Task sono sempre foglie
    }
    
    // Getters e Setters semplici
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { 
        this.status = status; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { 
        this.priority = priority; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return Objects.equals(id, task.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Task{id='%s', title='%s', status=%s}".formatted(id, title, status);
    }
}