package com.taskmanager.models;

/**
 * Enumerazione per gli stati dei task.
 * Esempio semplice di enum in Java.
 */
public enum TaskStatus {
    TODO("Da fare"),
    IN_PROGRESS("In corso"),
    DONE("Completato"),
    CANCELLED("Annullato");
    
    private final String displayName;
    
    TaskStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}