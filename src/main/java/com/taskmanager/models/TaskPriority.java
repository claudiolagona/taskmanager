package com.taskmanager.models;

/**
 * Enum per le priorità dei task.
 * 
 * @param LOW bassa priorità
 * @param MEDIUM media priorità
 * @param HIGH alta priorità
 * @param CRITICAL priorità critica
 * @param displayName nome visualizzato per la priorità
 * @param level livello numerico della priorità
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