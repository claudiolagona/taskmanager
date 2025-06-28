package com.taskmanager.proxy;

import com.taskmanager.models.Task;
import com.taskmanager.services.TaskManager;

import java.util.List;

/**
 * Proxy che aggiunge controlli di sicurezza al TaskManager per controllare l'accesso.
 * 
 * @param realTaskManager il TaskManager reale da prteggere
 * @param currentUser l'utente che sta tentando di accedere al TaskManager
 */
public class SecureTaskManager implements TaskManager {
    private final TaskManager realTaskManager;
    private final String currentUser;
    
    /**
     * Costruttore del proxy
     * 
     * @param realTaskManager il TaskManager reale
     * @param currentUser l'utente corrente
     */
    public SecureTaskManager(TaskManager realTaskManager, String currentUser) {
        this.realTaskManager = realTaskManager;
        this.currentUser = currentUser;
    }
    
    @Override
    public void addTask(Task task) {
        if (isValidUser()) {
            System.out.println("🔒 Controllo sicurezza OK per utente: " + currentUser);
            realTaskManager.addTask(task);
        } else {
            System.out.println("❌ Accesso negato per utente: " + currentUser);
            throw new SecurityException("Accesso negato per utente non valido");
        }
    }
    
    @Override
    public List<Task> getAllTasks() {
        if (isValidUser()) {
            return realTaskManager.getAllTasks();
        } else {
            System.out.println("❌ Accesso negato per visualizzare i task");
            return List.of(); // Lista vuota
        }
    }
    
    @Override
    public Task findTaskById(String id) {
        if (isValidUser()) {
            return realTaskManager.findTaskById(id);
        } else {
            System.out.println("❌ Accesso negato per cercare task");
            return null;
        }
    }
    
    @Override
    public boolean deleteTask(String id) {
        if (isValidUser()) {
            return realTaskManager.deleteTask(id);
        } else {
            System.out.println("❌ Accesso negato per eliminare task");
            return false;
        }
    }
    
    /**
     * Semplice controllo di validità utente
     * 
     * @return true se l'utente è valido
     */
    private boolean isValidUser() {
        // Controllo semplice: l'utente non deve essere null o vuoto
        return currentUser != null && !currentUser.trim().isEmpty();
    }
}