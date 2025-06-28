package com.taskmanager.proxy;

import com.taskmanager.models.Task;
import com.taskmanager.services.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.List;

/**
 * Test per SecureTaskManager - Pattern Proxy.
 */
@DisplayName("Test per SecureTaskManager - Proxy Pattern")
class SecureTaskManagerTest {
    
    private TaskManager mockTaskManager;
    private SecureTaskManager secureProxy;
    private Task testTask;
    
    @BeforeEach
    void setUp() {
        mockTaskManager = mock(TaskManager.class);
        testTask = new Task("Task Test", "Descrizione");
    }
    
    @Test
    @DisplayName("Dovrebbe permettere operazioni con utente valido")
    void shouldAllowOperationsWithValidUser() {
        // Arrange
        secureProxy = new SecureTaskManager(mockTaskManager, "utente.valido");
        
        // Act & Assert - addTask
        assertDoesNotThrow(() -> secureProxy.addTask(testTask), 
                          "AddTask dovrebbe funzionare con utente valido");
        verify(mockTaskManager, times(1)).addTask(testTask);
        
        // Act & Assert - getAllTasks
        when(mockTaskManager.getAllTasks()).thenReturn(Arrays.asList(testTask));
        List<Task> tasks = secureProxy.getAllTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        verify(mockTaskManager, times(1)).getAllTasks();
        
        // Act & Assert - findTaskById
        when(mockTaskManager.findTaskById("123")).thenReturn(testTask);
        Task found = secureProxy.findTaskById("123");
        assertNotNull(found);
        verify(mockTaskManager, times(1)).findTaskById("123");
        
        // Act & Assert - deleteTask
        when(mockTaskManager.deleteTask("123")).thenReturn(true);
        boolean deleted = secureProxy.deleteTask("123");
        assertTrue(deleted);
        verify(mockTaskManager, times(1)).deleteTask("123");
    }
    
    @Test
    @DisplayName("Dovrebbe bloccare operazioni con utente null")
    void shouldBlockOperationsWithNullUser() {
        // Arrange
        secureProxy = new SecureTaskManager(mockTaskManager, null);
        
        // Act & Assert - addTask dovrebbe lanciare SecurityException
        assertThrows(SecurityException.class, () -> secureProxy.addTask(testTask),
                    "AddTask dovrebbe lanciare SecurityException con utente null");
        verify(mockTaskManager, never()).addTask(any());
        
        // Act & Assert - getAllTasks dovrebbe restituire lista vuota
        List<Task> tasks = secureProxy.getAllTasks();
        assertTrue(tasks.isEmpty(), "GetAllTasks dovrebbe restituire lista vuota");
        verify(mockTaskManager, never()).getAllTasks();
        
        // Act & Assert - findTaskById dovrebbe restituire null
        Task found = secureProxy.findTaskById("123");
        assertNull(found, "FindTaskById dovrebbe restituire null");
        verify(mockTaskManager, never()).findTaskById(any());
        
        // Act & Assert - deleteTask dovrebbe restituire false
        boolean deleted = secureProxy.deleteTask("123");
        assertFalse(deleted, "DeleteTask dovrebbe restituire false");
        verify(mockTaskManager, never()).deleteTask(any());
    }
    
    @Test
    @DisplayName("Dovrebbe bloccare operazioni con utente vuoto")
    void shouldBlockOperationsWithEmptyUser() {
        // Test con string vuota
        secureProxy = new SecureTaskManager(mockTaskManager, "");
        
        assertThrows(SecurityException.class, () -> secureProxy.addTask(testTask));
        verify(mockTaskManager, never()).addTask(any());
        
        // Test con solo spazi
        secureProxy = new SecureTaskManager(mockTaskManager, "   ");
        
        assertThrows(SecurityException.class, () -> secureProxy.addTask(testTask));
        verify(mockTaskManager, never()).addTask(any());
    }
    
    @Test
    @DisplayName("Dovrebbe gestire eccezioni del TaskManager reale")
    void shouldHandleExceptionsFromRealTaskManager() {
        // Arrange
        secureProxy = new SecureTaskManager(mockTaskManager, "utente.valido");
        doThrow(new RuntimeException("Errore interno")).when(mockTaskManager).addTask(any());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> secureProxy.addTask(testTask),
                    "Dovrebbe propagare eccezioni del TaskManager reale");
        verify(mockTaskManager, times(1)).addTask(testTask);
    }
    
    @Test
    @DisplayName("Dovrebbe permettere operazioni di lettura anche con utente non valido")
    void shouldAllowReadOperationsEvenWithInvalidUser() {
        // Arrange - questo test riflette il comportamento attuale
        secureProxy = new SecureTaskManager(mockTaskManager, null);
        
        // getAllTasks restituisce lista vuota ma non lancia eccezione
        List<Task> tasks = secureProxy.getAllTasks();
        assertTrue(tasks.isEmpty());
        
        // findTaskById restituisce null ma non lancia eccezione
        Task found = secureProxy.findTaskById("123");
        assertNull(found);
        
        // deleteTask restituisce false ma non lancia eccezione
        boolean deleted = secureProxy.deleteTask("123");
        assertFalse(deleted);
        
        // Verifica che il TaskManager reale non sia mai chiamato
        verify(mockTaskManager, never()).getAllTasks();
        verify(mockTaskManager, never()).findTaskById(any());
        verify(mockTaskManager, never()).deleteTask(any());
    }
    
    @Test
    @DisplayName("Dovrebbe distinguere tra utenti con nomi diversi")
    void shouldDistinguishBetweenDifferentUserNames() {
        // Test con diversi nomi utente validi
        String[] validUsers = {"admin", "user123", "test.user", "a", "very-long-username"};
        
        for (String user : validUsers) {
            secureProxy = new SecureTaskManager(mockTaskManager, user);
            assertDoesNotThrow(() -> secureProxy.addTask(testTask), 
                             "Utente '" + user + "' dovrebbe essere valido");
        }
        
        // Verifica che addTask sia stato chiamato per ogni utente valido
        verify(mockTaskManager, times(validUsers.length)).addTask(testTask);
    }
}