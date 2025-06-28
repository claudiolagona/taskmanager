package com.taskmanager.iterator;

import com.taskmanager.models.Task;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterator personalizzato per i Task.
 * Implementa il pattern Iterator per attraversare collezioni.
 * 
 * @param tasks lista di task da iterare
 * @param currentIndex indice corrente dell'iterazione
 */
public class TaskIterator implements Iterator<Task> {
    private final List<Task> tasks;
    private int currentIndex = 0;
    
    /**
     * Costruttore
     * 
     * @param tasks lista di task da iterare
     */
    public TaskIterator(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : List.of();
    }
    
    @Override
    public boolean hasNext() {
        return currentIndex < tasks.size();
    }
    
    @Override
    public Task next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Nessun task successivo disponibile");
        }
        return tasks.get(currentIndex++);
    }
    
    /**
     * Reimposta l'iterator all'inizio
     */
    public void reset() {
        currentIndex = 0;
    }
    
    /**
     * Restituisce il numero totale di task
     * 
     * @return numero totale
     */
    public int getTotalCount() {
        return tasks.size();
    }
    
    /**
     * Restituisce l'indice corrente
     * 
     * @return indice corrente
     */
    public int getCurrentIndex() {
        return currentIndex;
    }
}