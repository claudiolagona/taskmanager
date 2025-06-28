package com.taskmanager.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Test per la classe Project - Pattern Composite (Composite).
 */
@DisplayName("Test per la classe Project")
class ProjectTest {
    
    private Project project;
    private Task task1;
    private Task task2;
    
    @BeforeEach
    void setUp() {
        project = new Project("Progetto Test", "Descrizione progetto");
        task1 = new Task("Task 1", "Descrizione 1");
        task2 = new Task("Task 2", "Descrizione 2");
    }
    
    @Test
    @DisplayName("Dovrebbe creare progetto vuoto")
    void shouldCreateEmptyProject() {
        assertNotNull(project.getId());
        assertEquals("Progetto Test", project.getTitle());
        assertEquals("Descrizione progetto", project.getDescription());
        assertFalse(project.isLeaf(), "Project non dovrebbe essere una foglia");
        assertTrue(project.getComponents().isEmpty(), "Progetto dovrebbe essere vuoto inizialmente");
    }
    
    @Test
    @DisplayName("Dovrebbe aggiungere e rimuovere componenti (Composite Pattern)")
    void shouldAddAndRemoveComponents() {
        // Test del pattern Composite
        
        // Act: aggiungi componenti
        project.addComponent(task1);
        project.addComponent(task2);
        
        // Assert: verifica aggiunta
        List<TaskComponent> components = project.getComponents();
        assertEquals(2, components.size(), "Dovrebbero esserci 2 componenti");
        assertTrue(components.contains(task1), "Dovrebbe contenere task1");
        assertTrue(components.contains(task2), "Dovrebbe contenere task2");
        
        // Act: rimuovi componente
        project.removeComponent(task1);
        
        // Assert: verifica rimozione
        assertEquals(1, project.getComponents().size(), "Dovrebbe rimanere 1 componente");
        assertFalse(project.getComponents().contains(task1), "Non dovrebbe più contenere task1");
        assertTrue(project.getComponents().contains(task2), "Dovrebbe ancora contenere task2");
    }
    
    @Test
    @DisplayName("Non dovrebbe aggiungere componenti null o duplicati")
    void shouldNotAddNullOrDuplicateComponents() {
        // Test aggiunta null
        project.addComponent(null);
        assertTrue(project.getComponents().isEmpty(), "Non dovrebbe aggiungere componenti null");
        
        // Test aggiunta duplicata
        project.addComponent(task1);
        project.addComponent(task1); // Duplicato
        
        assertEquals(1, project.getComponents().size(), "Non dovrebbe aggiungere duplicati");
    }
    
    @Test
    @DisplayName("Dovrebbe supportare progetti annidati (Composite Pattern)")
    void shouldSupportNestedProjects() {
        // Test della struttura ad albero del Composite
        Project sottoprogetto = new Project("Sottoprogetto", "Descrizione sottoprogetto");
        sottoprogetto.addComponent(task1);
        
        project.addComponent(sottoprogetto);
        project.addComponent(task2);
        
        assertEquals(2, project.getComponents().size(), "Progetto principale dovrebbe avere 2 componenti");
        assertTrue(project.getComponents().contains(sottoprogetto), "Dovrebbe contenere il sottoprogetto");
        assertTrue(project.getComponents().contains(task2), "Dovrebbe contenere task2");
        assertEquals(1, sottoprogetto.getComponents().size(), "Sottoprogetto dovrebbe avere 1 componente");
    }
    
    @Test
    @DisplayName("Dovrebbe restituire lista immutabile dei componenti")
    void shouldReturnImmutableComponentsList() {
        project.addComponent(task1);
        List<TaskComponent> components = project.getComponents();
        
        // Tentativo di modifica dovrebbe lanciare eccezione
        assertThrows(UnsupportedOperationException.class, () -> {
            components.add(task2);
        }, "Lista componenti dovrebbe essere immutabile");
    }
}