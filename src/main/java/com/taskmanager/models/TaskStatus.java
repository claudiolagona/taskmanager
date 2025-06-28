package com.taskmanager.models;

/**
 * Enum per gli stati dei task.
 * 
 * @param TODO stato del task da fare
 * @param IN_PROGRESS stato del task in corso
 * @param DONE stato del task completato
 * @param CANCELLED stato del task annullato
 * @param displayName nome visualizzato per lo stato del task
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