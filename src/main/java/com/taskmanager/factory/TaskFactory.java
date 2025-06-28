package com.taskmanager.factory;

import com.taskmanager.models.Task;
import com.taskmanager.models.Project;
import com.taskmanager.models.TaskComponent;

/**
 * Factory per creare Task e Project.
 * Implementa il pattern Factory Method.
 */
public class TaskFactory {
    
    /**
     * Crea un Task semplice
     * 
     * @param title titolo del task
     * @param description descrizione del task
     * @return nuovo Task
     */
    public static Task createTask(String title, String description) {
        return new Task(title, description);
    }
    
    /**
     * Crea un Task solo con titolo
     * 
     * @param title titolo del task
     * @return nuovo Task
     */
    public static Task createTask(String title) {
        return new Task(title, null);
    }
    
    /**
     * Crea un Project
     * 
     * @param title titolo del progetto
     * @param description descrizione del progetto
     * @return nuovo Project
     */
    public static Project createProject(String title, String description) {
        return new Project(title, description);
    }
    
    /**
     * Crea un Project solo con titolo
     * 
     * @param title titolo del progetto
     * @return nuovo Project
     */
    public static Project createProject(String title) {
        return new Project(title, null);
    }
    
    /**
     * Factory Method che crea il componente giusto in base al tipo
     * 
     * @param type "task" o "project"
     * @param title titolo
     * @param description descrizione
     * @return TaskComponent creato
     */
    public static TaskComponent createComponent(String type, String title, String description) {
        return switch (type.toLowerCase()) {
            case "task" -> createTask(title, description);
            case "project" -> createProject(title, description);
            default -> throw new IllegalArgumentException("Tipo non supportato: " + type);
        };
    }
}