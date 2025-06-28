package com.taskmanager.observer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test per TaskNotifier - Pattern Observer (Subject).
 */
@DisplayName("Test per TaskNotifier - Observer Pattern")
class TaskNotifierTest {
    
    private TaskNotifier notifier;
    private TaskObserver mockObserver1;
    private TaskObserver mockObserver2;
    
    @BeforeEach
    void setUp() {
        notifier = new TaskNotifier();
        mockObserver1 = mock(TaskObserver.class);
        mockObserver2 = mock(TaskObserver.class);
    }
    
    @Test
    @DisplayName("Dovrebbe aggiungere e rimuovere observer")
    void shouldAddAndRemoveObservers() {
        assertEquals(0, notifier.getObserverCount(), "Inizialmente non dovrebbero esserci observer");
        
        notifier.addObserver(mockObserver1);
        assertEquals(1, notifier.getObserverCount(), "Dovrebbe esserci 1 observer");
        
        notifier.addObserver(mockObserver2);
        assertEquals(2, notifier.getObserverCount(), "Dovrebbero esserci 2 observer");
        
        notifier.removeObserver(mockObserver1);
        assertEquals(1, notifier.getObserverCount(), "Dovrebbe rimanere 1 observer");
    }
    
    @Test
    @DisplayName("Non dovrebbe aggiungere observer null o duplicati")
    void shouldNotAddNullOrDuplicateObservers() {
        notifier.addObserver(null);
        assertEquals(0, notifier.getObserverCount(), "Non dovrebbe aggiungere observer null");
        
        notifier.addObserver(mockObserver1);
        notifier.addObserver(mockObserver1); // Duplicato
        assertEquals(1, notifier.getObserverCount(), "Non dovrebbe aggiungere duplicati");
    }
    
    @Test
    @DisplayName("Dovrebbe notificare tutti gli observer (Observer Pattern)")
    void shouldNotifyAllObservers() {
        // Setup
        notifier.addObserver(mockObserver1);
        notifier.addObserver(mockObserver2);
        
        String eventType = "TEST_EVENT";
        String message = "Test message";
        String taskId = "task123";
        
        // Act
        notifier.notifyObservers(eventType, message, taskId);
        
        // Assert - verifica che tutti gli observer siano stati notificati
        verify(mockObserver1, times(1)).onTaskEvent(eventType, message, taskId);
        verify(mockObserver2, times(1)).onTaskEvent(eventType, message, taskId);
    }
    
    @Test
    @DisplayName("Dovrebbe gestire eccezioni negli observer")
    void shouldHandleObserverExceptions() {
        // Setup observer che lancia eccezione
        TaskObserver faultyObserver = mock(TaskObserver.class);
        doThrow(new RuntimeException("Observer error")).when(faultyObserver)
                .onTaskEvent(anyString(), anyString(), anyString());
        
        notifier.addObserver(faultyObserver);
        notifier.addObserver(mockObserver1);
        
        // Act - notifica non dovrebbe fallire anche se un observer lancia eccezione
        assertDoesNotThrow(() -> {
            notifier.notifyObservers("TEST", "message", "id");
        }, "Notifica non dovrebbe fallire per eccezioni negli observer");
        
        // Assert - observer funzionante dovrebbe comunque essere notificato
        verify(mockObserver1, times(1)).onTaskEvent("TEST", "message", "id");
    }
}