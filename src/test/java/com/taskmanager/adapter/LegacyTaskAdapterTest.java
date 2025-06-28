package com.taskmanager.adapter;

import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.TaskPriority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per LegacyTaskAdapter - Pattern Adapter.
 */
@DisplayName("Test per LegacyTaskAdapter - Adapter Pattern")
class LegacyTaskAdapterTest {
    
    private LegacyTaskAdapter adapter;
    private Task testTask;
    
    @BeforeEach
    void setUp() {
        adapter = new LegacyTaskAdapter();
        testTask = new Task("Task Test", "Descrizione test");
        testTask.setStatus(TaskStatus.IN_PROGRESS);
        testTask.setPriority(TaskPriority.HIGH);
    }
    
    @Test
    @DisplayName("Dovrebbe convertire Task moderno in formato legacy")
    void shouldConvertModernTaskToLegacyFormat() {
        LegacyTaskAdapter.LegacyTask legacyTask = adapter.adaptToLegacy(testTask);
        
        assertNotNull(legacyTask, "LegacyTask non dovrebbe essere null");
        assertEquals(testTask.getId(), legacyTask.identifier);
        assertEquals(testTask.getTitle(), legacyTask.name);
        assertEquals(testTask.getDescription(), legacyTask.notes);
        assertEquals("ACTIVE", legacyTask.state); // IN_PROGRESS -> ACTIVE
        assertEquals(3, legacyTask.importance); // HIGH -> 3
    }
    
    @Test
    @DisplayName("Dovrebbe convertire tutti i tipi di status")
    void shouldConvertAllStatusTypes() {
        // Test conversione TODO
        testTask.setStatus(TaskStatus.TODO);
        LegacyTaskAdapter.LegacyTask legacy = adapter.adaptToLegacy(testTask);
        assertEquals("PENDING", legacy.state);
        
        // Test conversione DONE
        testTask.setStatus(TaskStatus.DONE);
        legacy = adapter.adaptToLegacy(testTask);
        assertEquals("COMPLETED", legacy.state);
        
        // Test conversione CANCELLED
        testTask.setStatus(TaskStatus.CANCELLED);
        legacy = adapter.adaptToLegacy(testTask);
        assertEquals("CANCELED", legacy.state);
    }
    
    @Test
    @DisplayName("Dovrebbe convertire tutte le priorità")
    void shouldConvertAllPriorities() {
        // Test LOW -> 1
        testTask.setPriority(TaskPriority.LOW);
        assertEquals(1, adapter.adaptToLegacy(testTask).importance);
        
        // Test MEDIUM -> 2
        testTask.setPriority(TaskPriority.MEDIUM);
        assertEquals(2, adapter.adaptToLegacy(testTask).importance);
        
        // Test HIGH -> 3
        testTask.setPriority(TaskPriority.HIGH);
        assertEquals(3, adapter.adaptToLegacy(testTask).importance);
        
        // Test CRITICAL -> 4
        testTask.setPriority(TaskPriority.CRITICAL);
        assertEquals(4, adapter.adaptToLegacy(testTask).importance);
    }
    
    @Test
    @DisplayName("Dovrebbe gestire task null")
    void shouldHandleNullTask() {
        LegacyTaskAdapter.LegacyTask result = adapter.adaptToLegacy(null);
        assertNull(result, "Risultato dovrebbe essere null per input null");
    }
}