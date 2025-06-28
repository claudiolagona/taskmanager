package com.taskmanager.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la classe Task - Pattern Composite (Leaf).
 * Testa tutte le funzionalità base del modello Task.
 */
@DisplayName("Test per la classe Task")
class TaskTest {
    
    private Task task;
    
    @BeforeEach
    @DisplayName("Setup test: crea task di esempio")
    void setUp() {
        task = new Task("Task di Test", "Descrizione di esempio");
    }
    
    @Test
    @DisplayName("Dovrebbe creare un task con i parametri base")
    void shouldCreateTaskWithBasicParameters() {
        assertNotNull(task.getId(), "ID del task non dovrebbe essere null");
        assertEquals("Task di Test", task.getTitle(), "Titolo dovrebbe corrispondere");
        assertEquals("Descrizione di esempio", task.getDescription(), "Descrizione dovrebbe corrispondere");
        assertEquals(TaskStatus.TODO, task.getStatus(), "Status iniziale dovrebbe essere TODO");
        assertEquals(TaskPriority.MEDIUM, task.getPriority(), "Priorità iniziale dovrebbe essere MEDIUM");
        assertNotNull(task.getCreatedAt(), "Data creazione non dovrebbe essere null");
        assertNotNull(task.getUpdatedAt(), "Data aggiornamento non dovrebbe essere null");
        assertTrue(task.isLeaf(), "Task dovrebbe essere una foglia nel Composite");
    }
    
    @Test
    @DisplayName("Dovrebbe lanciare eccezione per titolo null")
    void shouldThrowExceptionForNullTitle() {
        // Test che verifica la validazione degli input
        assertThrows(NullPointerException.class, () -> {
            new Task(null, "Descrizione");
        }, "Dovrebbe lanciare NullPointerException per titolo null");
    }
    
    @Test
    @DisplayName("Dovrebbe aggiornare le proprietà del task")
    void shouldUpdateTaskProperties() {
        String nuovoTitolo = "Titolo Aggiornato";
        TaskStatus nuovoStatus = TaskStatus.DONE;
        TaskPriority nuovaPriorita = TaskPriority.HIGH;
        
        task.setTitle(nuovoTitolo);
        task.setStatus(nuovoStatus);
        task.setPriority(nuovaPriorita);
        
        assertEquals(nuovoTitolo, task.getTitle());
        assertEquals(nuovoStatus, task.getStatus());
        assertEquals(nuovaPriorita, task.getPriority());
    }
    
    @Test
    @DisplayName("Dovrebbe implementare equals e hashCode correttamente")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        Task task1 = new Task("Task 1", "Descrizione 1");
        Task task2 = new Task("Task 2", "Descrizione 2");
        
        assertEquals(task1, task1, "Task dovrebbe essere uguale a se stesso");
        assertNotEquals(task1, task2, "Task diversi dovrebbero avere ID diversi");
        assertNotEquals(task1.hashCode(), task2.hashCode(), "HashCode dovrebbero essere diversi");
        assertNotEquals(task1, null, "Task non dovrebbe essere uguale a null");
        assertNotEquals(task1, "string", "Task non dovrebbe essere uguale a oggetti di tipo diverso");
    }
    
    @Test
    @DisplayName("Dovrebbe visualizzare informazioni del task senza errori")
    void shouldDisplayTaskInformationWithoutErrors() {
        // Test che il metodo display() non lanci eccezioni
        assertDoesNotThrow(() -> task.display(0), "Display con indentazione 0 non dovrebbe lanciare eccezioni");
        assertDoesNotThrow(() -> task.display(2), "Display con indentazione 2 non dovrebbe lanciare eccezioni");
    }
    
    @Test
    @DisplayName("Dovrebbe gestire descrizione null")
    void shouldHandleNullDescription() {
        // Test con descrizione null
        Task taskSenzaDescrizione = new Task("Solo Titolo", null);
        
        assertNotNull(taskSenzaDescrizione.getId());
        assertEquals("Solo Titolo", taskSenzaDescrizione.getTitle());
        assertNull(taskSenzaDescrizione.getDescription());
        
        // Il display dovrebbe funzionare anche con descrizione null
        assertDoesNotThrow(() -> taskSenzaDescrizione.display(0));
    }
}