package com.taskmanager.builder;

import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.TaskPriority;
import java.util.Objects;

/**
 * Builder per costruire Task complessi.
 * Implementa il pattern Builder - permette di costruire oggetti passo dopo passo.
 * 
 * @param title titolo del task (OBBLIGATORIO)
 * @param description descrizione del task (OPZIONALE)
 * @param status stato del task (OPZIONALE, default TODO)
 * @param priority priorità del task (OPZIONALE, default MEDIUM)
 */
public class TaskBuilder {
    private final String title;
    private String description;
    private TaskStatus status = TaskStatus.TODO;
    private TaskPriority priority = TaskPriority.MEDIUM;
    
    /**
     * Costruttore con titolo obbligatorio.
     * VALIDAZIONE: Il titolo non può essere null.
     * 
     * @param title titolo del task (OBBLIGATORIO, non può essere null)
     * @throws NullPointerException se title è null
     */
    public TaskBuilder(String title) {
        this.title = Objects.requireNonNull(title, "Il titolo non può essere null");
    }
    
    /**
     * Imposta la descrizione del task.
     * 
     * @param description descrizione del task
     * @return questo builder per concatenare le chiamate
     */
    public TaskBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    /**
     * Imposta lo status del task.
     * Se non specificato, il default è TODO.
     * 
     * @param status status del task
     * @return questo builder per concatenare le chiamate
     */
    public TaskBuilder withStatus(TaskStatus status) {
        this.status = status;
        return this;
    }
    
    /**
     * Imposta la priorità del task.
     * Se non specificata, la priorità di default è MEDIUM.
     * 
     * @param priority priorità del task
     * @return questo builder per concatenare le chiamate
     */
    public TaskBuilder withPriority(TaskPriority priority) {
        this.priority = priority;
        return this;
    }
    
    /**
     * Costruisce il Task finale.
     * Tutti i parametri devono essere stati impostati tramite i metodi del builder.
     * 
     * @return Task costruito con tutti i parametri impostati
     */
    public Task build() {
        Task task = new Task(title, description);
        task.setStatus(status);
        task.setPriority(priority);
        return task;
    }
}