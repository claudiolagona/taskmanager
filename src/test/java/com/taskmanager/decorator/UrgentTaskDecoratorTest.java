package com.taskmanager.decorator;

import com.taskmanager.models.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per UrgentTaskDecorator - Pattern Decorator.
 */
@DisplayName("Test per UrgentTaskDecorator - Decorator Pattern")
class UrgentTaskDecoratorTest {
    
    private Task baseTask;
    private UrgentTaskDecorator urgentTask;
    
    @BeforeEach
    void setUp() {
        baseTask = new Task("Task Normale", "Descrizione normale");
        urgentTask = new UrgentTaskDecorator(baseTask);
    }
    
    @Test
    @DisplayName("Dovrebbe aggiungere prefisso URGENTE al titolo")
    void shouldAddUrgentPrefixToTitle() {
        String urgentTitle = urgentTask.getTitle();
        assertTrue(urgentTitle.startsWith("🚨 URGENTE:"), 
                  "Titolo dovrebbe iniziare con prefisso urgente");
        assertTrue(urgentTitle.contains("Task Normale"), 
                  "Titolo dovrebbe contenere il titolo originale");
        assertEquals("🚨 URGENTE: Task Normale", urgentTitle);
    }
    
    @Test
    @DisplayName("Dovrebbe delegare le altre operazioni al task originale")
    void shouldDelegateOtherOperationsToOriginalTask() {
        // Test che il decorator deleghi correttamente
        assertEquals(baseTask.getId(), urgentTask.getId());
        assertEquals(baseTask.getDescription(), baseTask.getDescription());
        assertEquals(baseTask.getStatus(), urgentTask.getStatus());
        assertEquals(baseTask.getPriority(), urgentTask.getPriority());
        assertEquals(baseTask.isLeaf(), urgentTask.isLeaf());
    }
    
    @Test
    @DisplayName("Dovrebbe permettere modifica del task decorato")
    void shouldAllowModificationOfDecoratedTask() {
        urgentTask.setTitle("Nuovo Titolo");
        
        assertEquals("🚨 URGENTE: Nuovo Titolo", urgentTask.getTitle());
        assertEquals("Nuovo Titolo", baseTask.getTitle(), 
                    "Task base dovrebbe essere stato modificato");
    }
    
    @Test
    @DisplayName("Dovrebbe fornire messaggio di urgenza specifico")
    void shouldProvideSpecificUrgencyMessage() {
        String message = urgentTask.getUrgencyMessage();
        
        assertNotNull(message);
        assertFalse(message.trim().isEmpty());
        assertTrue(message.toLowerCase().contains("attenzione") || 
                  message.toLowerCase().contains("immediata"),
                  "Messaggio dovrebbe indicare urgenza");
    }
    
    @Test
    @DisplayName("Display dovrebbe mostrare formato urgente")
    void displayShouldShowUrgentFormat() {
        // Test che display() non lanci eccezioni e mostri formato urgente
        assertDoesNotThrow(() -> urgentTask.display(0), 
                          "Display non dovrebbe lanciare eccezioni");
        assertDoesNotThrow(() -> urgentTask.display(2), 
                          "Display con indentazione non dovrebbe lanciare eccezioni");
    }
}