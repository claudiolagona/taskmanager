package com.taskmanager.strategy;

import java.util.List;

/**
 * Interfaccia Strategy per ordinare le liste.
 */
public interface SortStrategy<T> {
    /**
     * Ordina una lista secondo la strategia implementata
     * 
     * @param items lista da ordinare
     * @return lista ordinata
     */
    List<T> sort(List<T> items);
    
    /**
     * Nome della strategia
     * @return nome descrittivo
     */
    String getStrategyName();
}
