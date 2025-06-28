package com.taskmanager.decorator;

import com.taskmanager.models.TaskComponent;

/**
 * Decorator concreto che aggiunge comportamento "urgente" ai task.
 */
public class UrgentTaskDecorator extends TaskDecorator {
    
    public UrgentTaskDecorator(TaskComponent component) {
        super(component);
    }
    
    @Override
    public String getTitle() {
        return "🚨 URGENTE: " + super.getTitle();
    }
    
    @Override
    public void display(int indentLevel) {
        String indent = "  ".repeat(indentLevel);
        System.out.println(indent + "⚡ === TASK URGENTE ===");
        super.display(indentLevel);
        System.out.println(indent + "⚡ === ATTENZIONE RICHIESTA ===");
    }
    
    /**
     * Metodo aggiuntivo specifico per task urgenti
     * @return messaggio di urgenza
     */
    public String getUrgencyMessage() {
        return "Questo task richiede attenzione immediata!";
    }
}