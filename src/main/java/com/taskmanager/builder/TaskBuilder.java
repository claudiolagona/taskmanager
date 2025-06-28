package com.taskmanager.builder;

import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.TaskPriority;
import java.util.Objects;

/**
 * Builder per costruire Task complessi.
 * Implementa il pattern Builder - permette di costruire oggetti passo dopo passo.
 */
public class TaskBuilder {
    // Campi del task da costruire
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
        // CORREZIONE: Aggiunta validazione nel costruttore
        this.title = Objects.requireNonNull(title, "Il titolo non può essere null");
    }
    
    /**
     * Imposta la descrizione (fluent interface)
     * @param description descrizione del task
     * @return questo builder per concatenare le chiamate
     */
    public TaskBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    /**
     * Imposta lo status
     * @param status status del task
     * @return questo builder
     */
    public TaskBuilder withStatus(TaskStatus status) {
        this.status = status;
        return this;
    }
    
    /**
     * Imposta la priorità
     * @param priority priorità del task
     * @return questo builder
     */
    public TaskBuilder withPriority(TaskPriority priority) {
        this.priority = priority;
        return this;
    }
    
    /**
     * Costruisce il Task finale
     * @return Task costruito con tutti i parametri impostati
     */
    public Task build() {
        Task task = new Task(title, description);
        task.setStatus(status);
        task.setPriority(priority);
        return task;
    }
}