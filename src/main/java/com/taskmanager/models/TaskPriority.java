package com.taskmanager.models;

/**
 * Enumerazione per le priorità dei task.
 */
public enum TaskPriority {
    LOW("Bassa", 1),
    MEDIUM("Media", 2), 
    HIGH("Alta", 3),
    CRITICAL("Critica", 4);
    
    private final String displayName;
    private final int level;
    
    TaskPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }
    
    public String getDisplayName() { return displayName; }
    public int getLevel() { return level; }
    
    @Override
    public String toString() {
        return displayName;
    }
}