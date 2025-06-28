package com.taskmanager.factory;

import com.taskmanager.models.Task;
import com.taskmanager.models.Project;
import com.taskmanager.models.TaskComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per TaskFactory - Pattern Factory Method.
 */
@DisplayName("Test per TaskFactory - Factory Method Pattern")
class TaskFactoryTest {
    
    @Test
    @DisplayName("Dovrebbe creare Task con Factory Method")
    void shouldCreateTaskWithFactoryMethod() {
        // Act: usa factory per creare task
        Task task = TaskFactory.createTask("Task da Factory", "Descrizione");
        
        // Assert: verifica creazione
        assertNotNull(task, "Factory dovrebbe creare task non null");
        assertEquals("Task da Factory", task.getTitle());
        assertEquals("Descrizione", task.getDescription());
        assertTrue(task instanceof Task, "Dovrebbe essere istanza di Task");
        assertTrue(task instanceof TaskComponent, "Dovrebbe implementare TaskComponent");
    }
    
    @Test
    @DisplayName("Dovrebbe creare Task senza descrizione")
    void shouldCreateTaskWithoutDescription() {
        Task task = TaskFactory.createTask("Solo Titolo");
        
        assertNotNull(task);
        assertEquals("Solo Titolo", task.getTitle());
        assertNull(task.getDescription(), "Descrizione dovrebbe essere null");
    }
    
    @Test
    @DisplayName("Dovrebbe creare Project con Factory Method")
    void shouldCreateProjectWithFactoryMethod() {
        Project project = TaskFactory.createProject("Progetto da Factory", "Descrizione progetto");
        
        assertNotNull(project);
        assertEquals("Progetto da Factory", project.getTitle());
        assertEquals("Descrizione progetto", project.getDescription());
        assertTrue(project instanceof Project);
        assertTrue(project instanceof TaskComponent);
        assertTrue(project.getComponents().isEmpty(), "Progetto dovrebbe essere vuoto inizialmente");
    }
    
    @Test
    @DisplayName("Dovrebbe creare componenti generici (Factory Method)")
    void shouldCreateGenericComponents() {
        // Test del metodo factory generico
        TaskComponent taskComponent = TaskFactory.createComponent("task", "Titolo Task", "Descrizione");
        TaskComponent projectComponent = TaskFactory.createComponent("project", "Titolo Project", "Descrizione");
        
        assertTrue(taskComponent instanceof Task, "Dovrebbe creare Task");
        assertTrue(projectComponent instanceof Project, "Dovrebbe creare Project");
        
        // Test tipo non supportato
        assertThrows(IllegalArgumentException.class, () -> {
            TaskFactory.createComponent("invalid", "Titolo", "Descrizione");
        }, "Dovrebbe lanciare eccezione per tipo non supportato");
    }
    
    @Test
    @DisplayName("Factory dovrebbe mantenere consistenza tra metodi")
    void factoryShouldMaintainConsistencyBetweenMethods() {
        // Test che diversi metodi factory producano risultati consistenti
        Task task1 = TaskFactory.createTask("Test", "Descrizione");
        Task task2 = (Task) TaskFactory.createComponent("task", "Test", "Descrizione");
        
        // Dovrebbero avere le stesse proprietà (eccetto ID che è unico)
        assertEquals(task1.getTitle(), task2.getTitle());
        assertEquals(task1.getDescription(), task2.getDescription());
        assertEquals(task1.getStatus(), task2.getStatus());
        assertEquals(task1.getPriority(), task2.getPriority());
        assertNotEquals(task1.getId(), task2.getId(), "ID dovrebbero essere diversi");
    }
}