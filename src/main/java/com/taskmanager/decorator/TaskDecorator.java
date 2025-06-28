package com.taskmanager.decorator;

import com.taskmanager.models.TaskComponent;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.TaskPriority;

/**
 * Decorator base per aggiungere funzionalità ai TaskComponent.
 * Implementa il pattern Decorator.
 */
public abstract class TaskDecorator implements TaskComponent {
    protected final TaskComponent wrappedComponent;
    
    /**
     * Costruttore del decorator
     * @param component il componente da decorare
     */
    protected TaskDecorator(TaskComponent component) {
        this.wrappedComponent = component;
    }
    
    // Delega le chiamate al componente wrappato
    @Override
    public String getId() {
        return wrappedComponent.getId();
    }
    
    @Override
    public String getTitle() {
        return wrappedComponent.getTitle();
    }
    
    @Override
    public void setTitle(String title) {
        wrappedComponent.setTitle(title);
    }
    
    @Override
    public void display(int indentLevel) {
        wrappedComponent.display(indentLevel);
    }
    
    @Override
    public boolean isLeaf() {
        return wrappedComponent.isLeaf();
    }
    
    // Delega i metodi per status e priority
    @Override
    public TaskStatus getStatus() {
        return wrappedComponent.getStatus();
    }
    
    @Override
    public void setStatus(TaskStatus status) {
        wrappedComponent.setStatus(status);
    }
    
    @Override
    public TaskPriority getPriority() {
        return wrappedComponent.getPriority();
    }
    
    @Override
    public void setPriority(TaskPriority priority) {
        wrappedComponent.setPriority(priority);
    }
}