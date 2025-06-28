package com.taskmanager.services;

import com.taskmanager.models.Task;
import java.util.List;

/**
 * Interfaccia del servizio principale per gestire i task.
 * Definisce le operazioni base CRUD (Create, Read, Update, Delete).
 */
public interface TaskManager {
    void addTask(Task task);
    List<Task> getAllTasks();
    Task findTaskById(String id);
    boolean deleteTask(String id);
}