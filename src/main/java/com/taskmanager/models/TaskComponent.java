package com.taskmanager.models;

/**
 * Interfaccia Component per il pattern Composite.
 * Definisce le operazioni comuni per Task e Project.
 */
public interface TaskComponent {
    String getId();
    String getTitle();
    void setTitle(String title);
    void display(int indentLevel);
    boolean isLeaf();
    TaskStatus getStatus();
    void setStatus(TaskStatus status);
    TaskPriority getPriority();
    void setPriority(TaskPriority priority);
}