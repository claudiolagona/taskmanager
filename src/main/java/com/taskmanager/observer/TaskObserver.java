package com.taskmanager.observer;

/**
 * Gli observer vengono notificati quando succede qualcosa ai task.
 */
public interface TaskObserver {
    /**
     * Metodo chiamato quando avviene un evento
     * @param eventType tipo di evento (es. "TASK_CREATED")
     * @param message messaggio descrittivo
     * @param taskId ID del task coinvolto
     */
    void onTaskEvent(String eventType, String message, String taskId);
}