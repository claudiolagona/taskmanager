package com.taskmanager.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Subject nel pattern Observer.
 * Mantiene una lista di observer e li notifica quando succede qualcosa.
 */
public class TaskNotifier {
    private final List<TaskObserver> observers = new ArrayList<>();
    
    /**
     * Aggiunge un observer
     * @param observer l'observer da aggiungere
     */
    public void addObserver(TaskObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Rimuove un observer
     * @param observer l'observer da rimuovere
     */
    public void removeObserver(TaskObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notifica tutti gli observer
     * @param eventType tipo di evento
     * @param message messaggio
     * @param taskId ID del task
     */
    public void notifyObservers(String eventType, String message, String taskId) {
        for (TaskObserver observer : observers) {
            try {
                observer.onTaskEvent(eventType, message, taskId);
            } catch (Exception e) {
                System.err.println("Errore notificando observer: " + e.getMessage());
            }
        }
    }
    
    /**
     * Restituisce il numero di observer registrati
     * @return numero di observer
     */
    public int getObserverCount() {
        return observers.size();
    }
}