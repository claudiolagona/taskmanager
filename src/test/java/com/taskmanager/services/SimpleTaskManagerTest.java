package com.taskmanager.services;

import com.taskmanager.factory.TaskFactory;
import com.taskmanager.models.Task;
import com.taskmanager.observer.TaskObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test per SimpleTaskManager - Business Logic.
 */
@DisplayName("Test per SimpleTaskManager - Business Logic")
class SimpleTaskManagerTest {
    
    private SimpleTaskManager taskManager;
    private TaskObserver mockObserver;
    
    @BeforeEach
    void setUp() {
        taskManager = new SimpleTaskManager();
        mockObserver = mock(TaskObserver.class);
        taskManager.addObserver(mockObserver);
    }
    
    @Test
    @DisplayName("Dovrebbe aggiungere task e notificare observer")
    void shouldAddTaskAndNotifyObserver() {
        // Arrange
        Task task = TaskFactory.createTask("Task Test", "Descrizione");
        
        // Act
        taskManager.addTask(task);
        
        // Assert
        assertEquals(1, taskManager.getTaskCount());
        assertNotNull(taskManager.findTaskById(task.getId()));
        
        // Verifica notifica observer (potrebbe richiedere un piccolo delay per async)
        verify(mockObserver, timeout(1000)).onTaskEvent(
            eq("TASK_CREATED"), 
            contains("Task Test"), 
            eq(task.getId())
        );
    }
    
    @Test
    @DisplayName("Dovrebbe rifiutare task null")
    void shouldRejectNullTask() {
        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask(null);
        }, "Dovrebbe lanciare IllegalArgumentException per task null");
        
        assertEquals(0, taskManager.getTaskCount());
    }
    
    @Test
    @DisplayName("Dovrebbe trovare task per ID")
    void shouldFindTaskById() {
        // Arrange
        Task task = TaskFactory.createTask("Task Ricercabile");
        taskManager.addTask(task);
        
        // Act
        Task found = taskManager.findTaskById(task.getId());
        
        // Assert
        assertNotNull(found);
        assertEquals(task.getId(), found.getId());
        assertEquals("Task Ricercabile", found.getTitle());
    }
    
    @Test
    @DisplayName("Dovrebbe restituire null per ID inesistente")
    void shouldReturnNullForNonExistentId() {
        Task found = taskManager.findTaskById("id-inesistente");
        assertNull(found);
        
        // Test anche con null e stringa vuota
        assertNull(taskManager.findTaskById(null));
        assertNull(taskManager.findTaskById(""));
        assertNull(taskManager.findTaskById("   "));
    }
    
    @Test
    @DisplayName("Dovrebbe eliminare task e notificare")
    void shouldDeleteTaskAndNotify() {
        // Arrange
        Task task = TaskFactory.createTask("Task da eliminare");
        taskManager.addTask(task);
        String taskId = task.getId();
        
        // Act
        boolean deleted = taskManager.deleteTask(taskId);
        
        // Assert
        assertTrue(deleted);
        assertEquals(0, taskManager.getTaskCount());
        assertNull(taskManager.findTaskById(taskId));
        
        // Verifica notifica eliminazione
        verify(mockObserver, timeout(1000)).onTaskEvent(
            eq("TASK_DELETED"),
            contains("eliminato"),
            eq(taskId)
        );
    }
    
    @Test
    @DisplayName("Dovrebbe restituire false per eliminazione di task inesistente")
    void shouldReturnFalseForDeletingNonExistentTask() {
        boolean deleted = taskManager.deleteTask("id-inesistente");
        assertFalse(deleted);
        
        // Test anche con null e stringa vuota
        assertFalse(taskManager.deleteTask(null));
        assertFalse(taskManager.deleteTask(""));
    }
    
    @Test
    @DisplayName("Dovrebbe restituire tutti i task")
    void shouldReturnAllTasks() {
        // Arrange
        Task task1 = TaskFactory.createTask("Task 1");
        Task task2 = TaskFactory.createTask("Task 2");
        Task task3 = TaskFactory.createTask("Task 3");
        
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        
        // Act
        var allTasks = taskManager.getAllTasks();
        
        // Assert
        assertEquals(3, allTasks.size());
        assertEquals(3, taskManager.getTaskCount());
        
        // Verifica che contenga tutti i task
        assertTrue(allTasks.stream().anyMatch(t -> t.getId().equals(task1.getId())));
        assertTrue(allTasks.stream().anyMatch(t -> t.getId().equals(task2.getId())));
        assertTrue(allTasks.stream().anyMatch(t -> t.getId().equals(task3.getId())));
    }
    
    @Test
    @DisplayName("Dovrebbe gestire lista vuota correttamente")
    void shouldHandleEmptyListCorrectly() {
        assertEquals(0, taskManager.getTaskCount());
        assertTrue(taskManager.getAllTasks().isEmpty());
    }
}