package com.taskmanager.storage;

import com.taskmanager.models.Task;
import com.taskmanager.models.Project;
import com.taskmanager.models.TaskComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per TaskStorage - Persistenza in memoria (versione semplificata).
 */
@DisplayName("Test per TaskStorage - In-Memory Storage")
class TaskStorageTest {
    
    private TaskStorage storage;
    private Task testTask;
    private Project testProject;
    
    @BeforeEach
    void setUp() {
        // Setup con storage in memoria per test isolati
        storage = new TaskStorage("test-tasks.json");
        storage.clear(); // Ci assicuriamo che sia vuoto
        
        testTask = new Task("Task Test", "Descrizione test");
        testProject = new Project("Project Test", "Descrizione progetto");
    }
    
    @AfterEach
    void tearDown() {
        // Cleanup dopo ogni test
        if (storage != null) {
            storage.clear();
        }
    }
    
    @Test
    @DisplayName("Dovrebbe salvare e recuperare task")
    void shouldSaveAndRetrieveTask() {
        // Test persistenza base
        storage.save(testTask);
        
        TaskComponent retrieved = storage.findById(testTask.getId());
        
        assertNotNull(retrieved, "Task salvato dovrebbe essere recuperabile");
        assertTrue(retrieved instanceof Task, "Dovrebbe essere un Task");
        assertEquals(testTask.getId(), retrieved.getId());
        assertEquals(testTask.getTitle(), retrieved.getTitle());
    }
    
    @Test
    @DisplayName("Dovrebbe gestire cache in memoria (Collections)")
    void shouldManageInMemoryCache() {
        // Test della cache thread-safe
        assertEquals(0, storage.size(), "Storage dovrebbe essere vuoto inizialmente");
        
        storage.save(testTask);
        assertEquals(1, storage.size(), "Storage dovrebbe contenere 1 elemento");
        
        storage.save(testProject);
        assertEquals(2, storage.size(), "Storage dovrebbe contenere 2 elementi");
        
        // Test che findAll() restituisca tutti gli elementi
        var allComponents = storage.findAll();
        assertEquals(2, allComponents.size());
    }
    
    @Test
    @DisplayName("Dovrebbe filtrare per tipo (Stream API)")
    void shouldFilterByType() {
        // Test Stream API e lambda expressions
        storage.save(testTask);
        storage.save(testProject);
        
        var tasks = storage.findAllTasks();
        var projects = storage.findAllProjects();
        
        assertEquals(1, tasks.size(), "Dovrebbe trovare 1 task");
        assertEquals(1, projects.size(), "Dovrebbe trovare 1 progetto");
        assertTrue(tasks.get(0) instanceof Task);
        assertTrue(projects.get(0) instanceof Project);
        
        // Verifica che i filtri funzionino correttamente
        assertEquals(testTask.getId(), tasks.get(0).getId());
        assertEquals(testProject.getId(), projects.get(0).getId());
    }
    
    @Test
    @DisplayName("Dovrebbe eliminare componenti")
    void shouldDeleteComponents() {
        // Test eliminazione normale
        storage.save(testTask);
        assertEquals(1, storage.size(), "Dovrebbe contenere 1 elemento prima dell'eliminazione");
        
        assertTrue(storage.delete(testTask.getId()), "Eliminazione dovrebbe riuscire");
        assertEquals(0, storage.size(), "Storage dovrebbe essere vuoto dopo eliminazione");
        assertNull(storage.findById(testTask.getId()), "Task eliminato non dovrebbe essere trovabile");
        
        // Test eliminazione di elementi inesistenti
        assertFalse(storage.delete("id-inesistente"), "Eliminazione di ID inesistente dovrebbe fallire");
        assertFalse(storage.delete(null), "Eliminazione di null dovrebbe fallire");
        assertFalse(storage.delete(""), "Eliminazione di stringa vuota dovrebbe fallire");
        assertFalse(storage.delete("   "), "Eliminazione di stringa con spazi dovrebbe fallire");
    }
    
    @Test
    @DisplayName("Dovrebbe gestire operazioni con parametri null/invalidi")
    void shouldHandleNullAndInvalidParameters() {
        // Test robustezza con input null/invalidi
        assertThrows(IllegalArgumentException.class, () -> {
            storage.save(null);
        }, "Save di null dovrebbe lanciare eccezione");
        
        // Test findById con parametri invalidi
        assertNull(storage.findById(null), "Find con ID null dovrebbe restituire null");
        assertNull(storage.findById(""), "Find con ID vuoto dovrebbe restituire null");
        assertNull(storage.findById("   "), "Find con ID solo spazi dovrebbe restituire null");
        assertNull(storage.findById("id-inesistente"), "Find con ID inesistente dovrebbe restituire null");
    }
    
    @Test
    @DisplayName("Dovrebbe fornire statistiche utili")
    void shouldProvideUsefulStatistics() {
        // Aggiungi elementi per test statistiche
        storage.save(testTask);
        storage.save(testProject);
        
        // Test che printStats() non lanci eccezioni
        assertDoesNotThrow(() -> storage.printStats(), 
                          "printStats() non dovrebbe lanciare eccezioni");
        
        // Verifica che le statistiche siano corrette
        assertEquals(2, storage.findAll().size(), "Dovrebbe avere 2 componenti totali");
        assertEquals(1, storage.findAllTasks().size(), "Dovrebbe avere 1 task");
        assertEquals(1, storage.findAllProjects().size(), "Dovrebbe avere 1 progetto");
        assertEquals(2, storage.size(), "Size() dovrebbe restituire 2");
    }
    
    @Test
    @DisplayName("Dovrebbe gestire storage vuoto correttamente")
    void shouldHandleEmptyStorageCorrectly() {
        // Test comportamento con storage vuoto
        assertEquals(0, storage.size(), "Storage vuoto dovrebbe avere size 0");
        assertTrue(storage.findAll().isEmpty(), "findAll() dovrebbe restituire lista vuota");
        assertTrue(storage.findAllTasks().isEmpty(), "findAllTasks() dovrebbe restituire lista vuota");
        assertTrue(storage.findAllProjects().isEmpty(), "findAllProjects() dovrebbe restituire lista vuota");
        
        // printStats() dovrebbe funzionare anche con storage vuoto
        assertDoesNotThrow(() -> storage.printStats());
    }
    
    @Test
    @DisplayName("Dovrebbe supportare operazioni multiple")
    void shouldSupportMultipleOperations() {
        // Test scenario più complesso con multiple operazioni
        
        // Crea più task e progetti
        Task task1 = new Task("Task 1", "Descrizione 1");
        Task task2 = new Task("Task 2", "Descrizione 2");
        Project project1 = new Project("Progetto 1", "Desc progetto 1");
        
        // Salva tutto
        storage.save(task1);
        storage.save(task2);
        storage.save(project1);
        
        assertEquals(3, storage.size());
        assertEquals(2, storage.findAllTasks().size());
        assertEquals(1, storage.findAllProjects().size());
        
        // Elimina un task
        assertTrue(storage.delete(task1.getId()));
        assertEquals(2, storage.size());
        assertEquals(1, storage.findAllTasks().size());
        assertEquals(1, storage.findAllProjects().size());
        
        // Verifica che il task giusto sia stato eliminato
        assertNull(storage.findById(task1.getId()));
        assertNotNull(storage.findById(task2.getId()));
        assertNotNull(storage.findById(project1.getId()));
        
        // Clear dovrebbe svuotare tutto
        storage.clear();
        assertEquals(0, storage.size());
        assertTrue(storage.findAll().isEmpty());
    }
    
    @Test
    @DisplayName("Dovrebbe mantenere l'integrità dei dati")
    void shouldMaintainDataIntegrity() {
        // Test che i dati salvati mantengano le loro proprietà
        storage.save(testTask);
        
        TaskComponent retrieved = storage.findById(testTask.getId());
        assertNotNull(retrieved);
        
        if (retrieved instanceof Task retrievedTask) {
            assertEquals(testTask.getId(), retrievedTask.getId());
            assertEquals(testTask.getTitle(), retrievedTask.getTitle());
            assertEquals(testTask.getDescription(), retrievedTask.getDescription());
            assertEquals(testTask.getStatus(), retrievedTask.getStatus());
            assertEquals(testTask.getPriority(), retrievedTask.getPriority());
        } else {
            fail("Il componente recuperato dovrebbe essere un Task");
        }
    }
}