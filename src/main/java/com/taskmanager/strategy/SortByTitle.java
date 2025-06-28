package com.taskmanager.strategy;

import com.taskmanager.models.TaskComponent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategia concreta per ordinare per titolo.
 * Implementa il pattern Strategy.
 */
public class SortByTitle implements SortStrategy<TaskComponent> {
    
    @Override
    public List<TaskComponent> sort(List<TaskComponent> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Usando Stream API e Lambda
        return items.stream()
                .sorted(Comparator.comparing(TaskComponent::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
    
    @Override
    public String getStrategyName() {
        return "Ordina per Titolo (A-Z)";
    }
}