package com.taskmanager.builder;

import com.taskmanager.models.Task;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.TaskPriority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per TaskBuilder - Pattern Builder.
 */
@DisplayName("Test per TaskBuilder - Builder Pattern")
class TaskBuilderTest {
    
    @Test
    @DisplayName("Dovrebbe costruire task con configurazione minima")
    void shouldBuildTaskWithMinimalConfiguration() {
        // Test Builder con solo titolo (configurazione minima)
        Task task = new TaskBuilder("Task Minimo").build();
        
        assertEquals("Task Minimo", task.getTitle());
        assertNull(task.getDescription(), "Descrizione dovrebbe essere null di default");
        assertEquals(TaskStatus.TODO, task.getStatus(), "Status dovrebbe essere TODO di default");
        assertEquals(TaskPriority.MEDIUM, task.getPriority(), "Priorità dovrebbe essere MEDIUM di default");
    }
    
    @Test
    @DisplayName("Dovrebbe costruire task con configurazione completa")
    void shouldBuildTaskWithFullConfiguration() {
        // Test Builder con tutte le opzioni (fluent interface)
        Task task = new TaskBuilder("Task Completo")
                .withDescription("Descrizione dettagliata")
                .withStatus(TaskStatus.IN_PROGRESS)
                .withPriority(TaskPriority.HIGH)
                .build();
        
        assertEquals("Task Completo", task.getTitle());
        assertEquals("Descrizione dettagliata", task.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(TaskPriority.HIGH, task.getPriority());
    }
    
    @Test
    @DisplayName("Dovrebbe supportare method chaining (fluent interface)")
    void shouldSupportMethodChaining() {
        // Test del method chaining caratteristico del Builder pattern
        TaskBuilder builder = new TaskBuilder("Task Concatenato")
                .withDescription("Test concatenazione")
                .withPriority(TaskPriority.LOW)
                .withStatus(TaskStatus.DONE);
        
        assertNotNull(builder, "Builder dovrebbe supportare method chaining");
        
        Task task = builder.build();
        assertEquals("Task Concatenato", task.getTitle());
        assertEquals("Test concatenazione", task.getDescription());
        assertEquals(TaskPriority.LOW, task.getPriority());
        assertEquals(TaskStatus.DONE, task.getStatus());
    }
    
    @Test
    @DisplayName("Dovrebbe creare istanze indipendenti")
    void shouldCreateIndependentInstances() {
        // Test che il Builder crei istanze separate
        TaskBuilder builder = new TaskBuilder("Template");
        
        Task task1 = builder.withDescription("Descrizione 1").build();
        Task task2 = builder.withDescription("Descrizione 2").build();
        
        assertNotEquals(task1.getId(), task2.getId(), "Task dovrebbero avere ID diversi");
        assertEquals("Template", task1.getTitle());
        assertEquals("Template", task2.getTitle());
        assertEquals("Descrizione 1", task1.getDescription(), 
                    "Ultima descrizione impostata dovrebbe essere applicata");
        assertEquals("Descrizione 2", task2.getDescription());
    }
    
    @Test
    @DisplayName("Builder dovrebbe validare parametri obbligatori")
    void builderShouldValidateRequiredParameters() {
        // Test validazione del Builder
        assertThrows(NullPointerException.class, () -> {
            new TaskBuilder(null);
        }, "Builder dovrebbe rifiutare titolo null");
        
        // Test che il build() funzioni con solo titolo
        TaskBuilder builder = new TaskBuilder("Titolo Valido");
        assertDoesNotThrow(() -> builder.build(), "Build dovrebbe funzionare con solo titolo");
    }
}