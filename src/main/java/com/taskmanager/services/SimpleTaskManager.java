package com.taskmanager.services;

import com.taskmanager.models.Task;
import com.taskmanager.storage.TaskStorage;
import com.taskmanager.observer.TaskNotifier;
import com.taskmanager.observer.TaskObserver;
import com.taskmanager.config.AppConfig;

import java.util.List;

/**
 * Implementazione semplice del TaskManager.
 * Usa TaskStorage per la persistenza e TaskNotifier per le notifiche.
 * 
 * @param storage il TaskStorage per la persistenza dei task
 * @param notifier il TaskNotifier per le notifiche agli observer
 */
public class SimpleTaskManager implements TaskManager {
    private final TaskStorage storage;
    private final TaskNotifier notifier;
    
    /**
     * Costruttore
     */
    public SimpleTaskManager() {
        // Usa la configurazione singleton per il nome del file
        String fileName = AppConfig.getInstance().getProperty("storage.file", "tasks.json");
        this.storage = new TaskStorage(fileName);
        this.notifier = new TaskNotifier();
    }
    
    @Override
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Il task non può essere null");
        }
        
        storage.save(task);
        notifier.notifyObservers("TASK_CREATED", "Nuovo task creato: " + task.getTitle(), task.getId());
        System.out.println("✅ Task aggiunto: " + task.getTitle());
    }
    
    @Override
    public List<Task> getAllTasks() {
        return storage.findAllTasks();
    }
    
    @Override
    public Task findTaskById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        
        var component = storage.findById(id);
        if (component instanceof Task task) {
            return task;
        }
        return null;
    }
    
    @Override
    public boolean deleteTask(String id) {
        Task task = findTaskById(id);
        if (task != null) {
            boolean deleted = storage.delete(id);
            if (deleted) {
                notifier.notifyObservers("TASK_DELETED", "Task eliminato: " + task.getTitle(), id);
                System.out.println("🗑️  Task eliminato: " + task.getTitle());
            }
            return deleted;
        }
        return false;
    }
    
    /**
     * Aggiunge un observer per le notifiche
     * 
     * @param observer l'observer da aggiungere
     */
    public void addObserver(TaskObserver observer) {
        notifier.addObserver(observer);
    }
    
    /**
     * Restituisce il numero di task salvati
     * 
     * @return numero di task
     */
    public int getTaskCount() {
        return storage.findAllTasks().size();
    }
    
    /**
     * Pulisce tutti i task
     */
    public void clearAllTasks() {
        storage.clear();
        notifier.notifyObservers("STORAGE_CLEARED", "Tutti i task sono stati eliminati", "SYSTEM");
        System.out.println("🧹 Tutti i task eliminati");
    }
}