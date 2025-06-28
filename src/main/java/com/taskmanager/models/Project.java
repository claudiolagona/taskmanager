package com.taskmanager.models;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classe che rappresenta un Progetto contenente task e altri progetti.
 * È il "Composite" nel pattern Composite.
 */
public class Project implements TaskComponent {
    private final String id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Lista thread-safe per contenere i componenti
    private final List<TaskComponent> components;
    
    /**
     * Costruttore per creare un nuovo progetto
     * @param title titolo del progetto
     * @param description descrizione del progetto
     */
    public Project(String title, String description) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.title = Objects.requireNonNull(title, "Il titolo non può essere null");
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.components = new CopyOnWriteArrayList<>(); // Thread-safe per multithreading
    }
    
    /**
     * Aggiunge un componente al progetto (Composite Pattern)
     * @param component il componente da aggiungere
     */
    public void addComponent(TaskComponent component) {
        if (component != null && !components.contains(component)) {
            components.add(component);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Rimuove un componente dal progetto
     * @param component il componente da rimuovere
     */
    public void removeComponent(TaskComponent component) {
        if (components.remove(component)) {
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Restituisce la lista dei componenti (Collections)
     * @return lista immutabile dei componenti
     */
    public List<TaskComponent> getComponents() {
        return Collections.unmodifiableList(components);
    }
    
    // Implementazione di TaskComponent
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
        System.out.printf("%s📁 Progetto: %s (%d componenti)%n", 
                         indent, title, components.size());
        
        if (description != null && !description.isEmpty()) {
            System.out.printf("%s   Descrizione: %s%n", indent, description);
        }
        
        // Mostra tutti i componenti (ricorsione per Composite)
        for (TaskComponent component : components) {
            component.display(indentLevel + 1);
        }
    }
    
    @Override
    public boolean isLeaf() {
        return false; // I progetti non sono mai foglie
    }
    
    // Implementazione dei metodi per status e priority
    // Per i progetti, restituiamo valori di default dato che sono concetti per i task
    @Override
    public TaskStatus getStatus() {
        // I progetti non hanno uno status specifico, restituiamo IN_PROGRESS se hanno componenti
        return components.isEmpty() ? TaskStatus.TODO : TaskStatus.IN_PROGRESS;
    }
    
    @Override
    public void setStatus(TaskStatus status) {
        // Per i progetti, impostiamo lo status a tutti i componenti che sono Task
        for (TaskComponent component : components) {
            if (component instanceof Task) {
                component.setStatus(status);
            }
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public TaskPriority getPriority() {
        // Restituisce la priorità più alta tra tutti i task contenuti
        TaskPriority maxPriority = TaskPriority.LOW;
        for (TaskComponent component : components) {
            TaskPriority componentPriority = component.getPriority();
            if (componentPriority.getLevel() > maxPriority.getLevel()) {
                maxPriority = componentPriority;
            }
        }
        return maxPriority;
    }
    
    @Override
    public void setPriority(TaskPriority priority) {
        // Per i progetti, impostiamo la priorità a tutti i componenti
        for (TaskComponent component : components) {
            component.setPriority(priority);
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters e Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description; 
        this.updatedAt = LocalDateTime.now();
    }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project project)) return false;
        return Objects.equals(id, project.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Project{id='%s', title='%s', components=%d}".formatted(id, title, components.size());
    }
}