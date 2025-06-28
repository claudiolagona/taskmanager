package com.taskmanager.iterator;

import com.taskmanager.models.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Test per TaskIterator - Pattern Iterator.
 */
@DisplayName("Test per TaskIterator - Iterator Pattern")
class TaskIteratorTest {
    
    private List<Task> tasks;
    private TaskIterator iterator;
    
    @BeforeEach
    void setUp() {
        tasks = Arrays.asList(
            new Task("Task 1", "Prima task"),
            new Task("Task 2", "Seconda task"),
            new Task("Task 3", "Terza task")
        );
        iterator = new TaskIterator(tasks);
    }
    
    @Test
    @DisplayName("Dovrebbe iterare attraverso tutti gli elementi")
    void shouldIterateThroughAllElements() {
        // Test basic iteration
        int count = 0;
        while (iterator.hasNext()) {
            Task task = iterator.next();
            assertNotNull(task, "Task non dovrebbe essere null");
            assertEquals(tasks.get(count).getTitle(), task.getTitle(), 
                        "Task dovrebbe corrispondere alla posizione nell'array");
            count++;
        }
        
        assertEquals(3, count, "Dovrebbe iterare su tutti e 3 i task");
    }
    
    @Test
    @DisplayName("Dovrebbe lanciare eccezione quando non ci sono più elementi")
    void shouldThrowExceptionWhenNoMoreElements() {
        // Consuma tutti gli elementi
        while (iterator.hasNext()) {
            iterator.next();
        }
        
        // Tentativo di leggere oltre la fine dovrebbe lanciare eccezione
        assertThrows(NoSuchElementException.class, () -> {
            iterator.next();
        }, "next() oltre la fine dovrebbe lanciare NoSuchElementException");
    }
    
    @Test
    @DisplayName("Dovrebbe fornire informazioni su conteggio e posizione")
    void shouldProvideCountAndPositionInformation() {
        assertEquals(3, iterator.getTotalCount(), "Conteggio totale dovrebbe essere 3");
        assertEquals(0, iterator.getCurrentIndex(), "Indice iniziale dovrebbe essere 0");
        
        iterator.next();
        assertEquals(1, iterator.getCurrentIndex(), "Dopo next() indice dovrebbe essere 1");
        
        iterator.next();
        iterator.next();
        assertEquals(3, iterator.getCurrentIndex(), "Alla fine indice dovrebbe essere 3");
    }
    
    @Test
    @DisplayName("Dovrebbe permettere reset dell'iterazione")
    void shouldAllowResettingIteration() {
        // Avanza l'iterator
        iterator.next();
        iterator.next();
        assertEquals(2, iterator.getCurrentIndex());
        
        // Reset
        iterator.reset();
        assertEquals(0, iterator.getCurrentIndex(), "Dopo reset indice dovrebbe essere 0");
        assertTrue(iterator.hasNext(), "Dopo reset dovrebbe avere elementi");
        
        // Dovrebbe poter iterare di nuovo
        Task firstTask = iterator.next();
        assertEquals(tasks.get(0).getTitle(), firstTask.getTitle(), 
                    "Dopo reset dovrebbe iniziare dal primo elemento");
    }
    
    @Test
    @DisplayName("Dovrebbe gestire liste vuote")
    void shouldHandleEmptyLists() {
        TaskIterator emptyIterator = new TaskIterator(Arrays.asList());
        
        assertFalse(emptyIterator.hasNext(), "Iterator vuoto non dovrebbe avere elementi");
        assertEquals(0, emptyIterator.getTotalCount(), "Conteggio dovrebbe essere 0");
        assertEquals(0, emptyIterator.getCurrentIndex(), "Indice dovrebbe essere 0");
        
        assertThrows(NoSuchElementException.class, () -> {
            emptyIterator.next();
        }, "next() su iterator vuoto dovrebbe lanciare eccezione");
    }
    
    @Test
    @DisplayName("Dovrebbe gestire parametri null")
    void shouldHandleNullParameters() {
        TaskIterator nullIterator = new TaskIterator(null);
        
        assertFalse(nullIterator.hasNext(), "Iterator con null non dovrebbe avere elementi");
        assertEquals(0, nullIterator.getTotalCount(), "Conteggio dovrebbe essere 0");
        
        assertThrows(NoSuchElementException.class, () -> {
            nullIterator.next();
        }, "next() su iterator null dovrebbe lanciare eccezione");
    }
}