package com.taskmanager.adapter;

import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.TaskPriority;

/**
 * Adapter per convertire Task nel formato di un sistema legacy.
 */
public class LegacyTaskAdapter {
    
    /**
     * Converte un Task moderno in formato legacy
     * 
     * @param task il task moderno
     * @return task in formato legacy
     */
    public LegacyTask adaptToLegacy(Task task) {
        if (task == null) return null;
        
        LegacyTask legacyTask = new LegacyTask();
        legacyTask.identifier = task.getId();
        legacyTask.name = task.getTitle();
        legacyTask.notes = task.getDescription();
        legacyTask.state = convertStatus(task.getStatus());
        legacyTask.importance = convertPriority(task.getPriority());
        
        return legacyTask;
    }
    
    /**
     * Converte uno status moderno in formato legacy
     * 
     * @param status lo stato non legacy
     * @return lo stato in formato legacy
     */
    private String convertStatus(TaskStatus status) {
        return switch (status) {
            case TODO -> "PENDING";
            case IN_PROGRESS -> "ACTIVE";
            case DONE -> "COMPLETED";
            case CANCELLED -> "CANCELED";
        };
    }
    
    /**
     * Converte una priorità moderna in formato legacy
     * 
     * @param priority la priorità non legacy
     * @return la priorità in formato legacy
     */
    private int convertPriority(TaskPriority priority) {
        return switch (priority) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
            case CRITICAL -> 4;
        };
    }
    
    /**
     * Classe interna che rappresenta il formato legacy
     * 
     * @param identifier l'ID del task
     * @param name il nome del task
     * @param notes le note del task
     * @param state lo stato del task in formato legacy
     * @param importance la priorità del task in formato legacy
     */
    public static class LegacyTask {
        public String identifier;
        public String name;
        public String notes;
        public String state;
        public int importance;
        
        @Override
        public String toString() {
            return "LegacyTask{id='%s', name='%s', state='%s', importance=%d}"
                    .formatted(identifier, name, state, importance);
        }
    }
}