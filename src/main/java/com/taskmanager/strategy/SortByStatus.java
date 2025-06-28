package com.taskmanager.strategy;

import com.taskmanager.models.TaskComponent;
import com.taskmanager.models.TaskStatus;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategia concreta per ordinare per status (TODO → IN_PROGRESS → DONE → CANCELLED).
 */
public class SortByStatus implements SortStrategy<TaskComponent> {
    
    @Override
    public List<TaskComponent> sort(List<TaskComponent> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        
        return items.stream()
                .sorted(Comparator.comparing(TaskComponent::getStatus, 
                       Comparator.comparing(this::getStatusOrder)))
                .toList();
    }
    
    /**
     * Definisce l'ordine dei status per l'ordinamento
     */
    private int getStatusOrder(TaskStatus status) {
        return switch (status) {
            case TODO -> 1;
            case IN_PROGRESS -> 2;
            case DONE -> 3;
            case CANCELLED -> 4;
        };
    }
    
    @Override
    public String getStrategyName() {
        return "Ordina per Status (TODO → IN_PROGRESS → DONE → CANCELLED)";
    }
}