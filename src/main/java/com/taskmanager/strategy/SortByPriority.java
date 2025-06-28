package com.taskmanager.strategy;

import com.taskmanager.models.TaskComponent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategia concreta per ordinare per priorità (dalla più alta alla più bassa).
 */
public class SortByPriority implements SortStrategy<TaskComponent> {
    
    @Override
    public List<TaskComponent> sort(List<TaskComponent> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        
        return items.stream()
                .sorted(Comparator.comparing(TaskComponent::getPriority, 
                       Comparator.comparing(priority -> -priority.getLevel()))) // Ordine decrescente
                .toList();
    }
    
    @Override
    public String getStrategyName() {
        return "Ordina per Priorità (Alta → Bassa)";
    }
}