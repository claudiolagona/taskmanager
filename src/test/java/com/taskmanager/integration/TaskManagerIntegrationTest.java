package com.taskmanager.integration;

import com.taskmanager.builder.TaskBuilder;
import com.taskmanager.decorator.UrgentTaskDecorator;
import com.taskmanager.factory.TaskFactory;
import com.taskmanager.iterator.TaskIterator;
import com.taskmanager.models.Task;
import com.taskmanager.models.Project;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.models.TaskPriority;
import com.taskmanager.observer.ConsoleObserver;
import com.taskmanager.proxy.SecureTaskManager;
import com.taskmanager.services.SimpleTaskManager;
import com.taskmanager.strategy.SortByTitle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test di integrazione che verifica il funzionamento combinato di tutti i pattern.
 */
@DisplayName("Test di Integrazione - Tutti i Pattern")
class TaskManagerIntegrationTest {
    
    private SimpleTaskManager taskManager;
    private SecureTaskManager secureTaskManager;
    
    @BeforeEach
    void setUp() {
        taskManager = new SimpleTaskManager();
        taskManager.addObserver(new ConsoleObserver());
        secureTaskManager = new SecureTaskManager(taskManager, "user.test");
        
        // Pulisce lo storage prima di ogni test
        taskManager.clearAllTasks();
        
        // Verifica che sia effettivamente vuoto
        assertEquals(0, taskManager.getTaskCount(), "Lo storage dovrebbe essere vuoto all'inizio del test");
    }
    
    @Test
    @DisplayName("Dovrebbe integrare Factory + Builder + Observer + Proxy")
    void shouldIntegrateFactoryBuilderObserverProxy() {
        // Crea task con Factory
        Task factoryTask = TaskFactory.createTask("Task da Factory", "Creato con Factory");
        
        // Crea task con Builder
        Task builderTask = new TaskBuilder("Task da Builder")
                .withDescription("Creato con Builder")
                .withPriority(TaskPriority.HIGH)
                .withStatus(TaskStatus.IN_PROGRESS)
                .build();
        
        // Aggiungi tramite Proxy (che testa sicurezza e notifica Observer)
        secureTaskManager.addTask(factoryTask);
        secureTaskManager.addTask(builderTask);
        
        // Verifica integrazione
        assertEquals(2, taskManager.getTaskCount());
        var allTasks = secureTaskManager.getAllTasks();
        assertEquals(2, allTasks.size());
    }
    
    @Test
    @DisplayName("Dovrebbe integrare Composite + Decorator + Iterator")
    void shouldIntegrateCompositeDecoratorIterator() {
        // Crea struttura Composite
        Project mainProject = new Project("Progetto Principale", "Progetto con sottoprogetti");
        Project subProject = new Project("Sottoprogetto", "Progetto annidato");
        
        Task normalTask = TaskFactory.createTask("Task Normale", "Task semplice");
        Task urgentTask = TaskFactory.createTask("Task Urgente", "Task importante");
        
        // Applica Decorator
        UrgentTaskDecorator decoratedTask = new UrgentTaskDecorator(urgentTask);
        
        // Costruisci gerarchia Composite
        subProject.addComponent(normalTask);
        mainProject.addComponent(subProject);
        mainProject.addComponent(decoratedTask);
        
        // Verifica struttura Composite
        assertEquals(2, mainProject.getComponents().size());
        assertEquals(1, subProject.getComponents().size());
        assertFalse(mainProject.isLeaf());
        assertTrue(normalTask.isLeaf());
        
        // Test Decorator
        assertTrue(decoratedTask.getTitle().contains("🚨 URGENTE:"));
        assertNotNull(decoratedTask.getUrgencyMessage());
        
        // Test Iterator con task salvati
        taskManager.addTask(normalTask);
        taskManager.addTask(urgentTask);
        
        TaskIterator iterator = new TaskIterator(taskManager.getAllTasks());
        int count = 0;
        while (iterator.hasNext()) {
            Task task = iterator.next();
            assertNotNull(task);
            count++;
        }
        assertEquals(2, count);
    }
    
    @Test
    @DisplayName("Dovrebbe integrare Strategy + Collections + Stream API")
    void shouldIntegrateStrategyCollectionsStreamAPI() {
        // Crea task con titoli per test ordinamento
        Task taskZ = TaskFactory.createTask("Z Task", "Ultimo alfabeticamente");
        Task taskA = TaskFactory.createTask("A Task", "Primo alfabeticamente");
        Task taskM = TaskFactory.createTask("M Task", "Medio alfabeticamente");
        
        // Aggiungi in ordine non alfabetico
        taskManager.addTask(taskZ);
        taskManager.addTask(taskA);
        taskManager.addTask(taskM);
        
        // Usa Strategy per ordinare
        SortByTitle sortStrategy = new SortByTitle();
        var sortedComponents = sortStrategy.sort(
            taskManager.getAllTasks().stream()
                .map(t -> (com.taskmanager.models.TaskComponent) t)
                .toList()
        );
        
        // Verifica ordinamento
        assertEquals(3, sortedComponents.size());
        assertEquals("A Task", sortedComponents.get(0).getTitle());
        assertEquals("M Task", sortedComponents.get(1).getTitle());
        assertEquals("Z Task", sortedComponents.get(2).getTitle());
        
        // Verifica che Strategy abbia nome
        assertNotNull(sortStrategy.getStrategyName());
        assertFalse(sortStrategy.getStrategyName().trim().isEmpty());
    }
    
    @Test
    @DisplayName("Dovrebbe gestire flusso completo di lavoro")
    void shouldHandleCompleteWorkflow() {
        // Scenario: creazione progetto complesso con workflow completo
        
        // 1. Crea progetto principale
        Project webProject = new Project("Sviluppo Website", "Progetto sviluppo sito web");
        
        // 2. Crea task con diversi pattern
        Task designTask = new TaskBuilder("Design UI/UX")
                .withDescription("Progettare interfaccia utente")
                .withPriority(TaskPriority.HIGH)
                .build();
        
        Task codingTask = TaskFactory.createTask("Implementazione", "Scrivere il codice");
        codingTask.setStatus(TaskStatus.IN_PROGRESS);
        
        Task testingTask = TaskFactory.createTask("Testing", "Test del sistema");
        UrgentTaskDecorator urgentTesting = new UrgentTaskDecorator(testingTask);
        
        // 3. Costruisci struttura
        webProject.addComponent(designTask);
        webProject.addComponent(codingTask);
        webProject.addComponent(urgentTesting);
        
        // 4. Salva task nel sistema tramite Proxy
        secureTaskManager.addTask(designTask);
        secureTaskManager.addTask(codingTask);
        secureTaskManager.addTask(testingTask);
        
        // 5. Verifica tutto il flusso
        assertEquals(3, taskManager.getTaskCount());
        assertEquals(3, webProject.getComponents().size());
        
        // Verifica che tutti i task siano recuperabili
        assertNotNull(secureTaskManager.findTaskById(designTask.getId()));
        assertNotNull(secureTaskManager.findTaskById(codingTask.getId()));
        assertNotNull(secureTaskManager.findTaskById(testingTask.getId()));
        
        // Verifica Decorator
        assertTrue(urgentTesting.getTitle().contains("URGENTE"));
        
        // Test display dell'intero progetto (non lancia eccezioni)
        assertDoesNotThrow(() -> webProject.display(0));
        
        // Test Iterator su tutti i task
        TaskIterator iterator = new TaskIterator(taskManager.getAllTasks());
        assertEquals(3, iterator.getTotalCount());
        
        // Test eliminazione
        boolean deleted = secureTaskManager.deleteTask(testingTask.getId());
        assertTrue(deleted);
        assertEquals(2, taskManager.getTaskCount());
    }
    
    @Test
    @DisplayName("Dovrebbe dimostrare pattern principali implementati")
    void shouldDemonstratePrincipalPatterns() {
        // Test che i pattern principali siano implementati e funzionanti
        
        // 1. Factory Method - TaskFactory
        Task factoryTask = TaskFactory.createTask("Factory Test");
        assertNotNull(factoryTask);
        assertEquals("Factory Test", factoryTask.getTitle());
        
        // 2. Builder - TaskBuilder
        Task builderTask = new TaskBuilder("Builder Test")
                .withPriority(TaskPriority.HIGH)
                .build();
        assertNotNull(builderTask);
        assertEquals("Builder Test", builderTask.getTitle());
        assertEquals(TaskPriority.HIGH, builderTask.getPriority());
        
        // 3. Composite - Project + Task
        Project project = new Project("Composite Test", "Test");
        project.addComponent(factoryTask);
        assertEquals(1, project.getComponents().size());
        assertFalse(project.isLeaf());
        assertTrue(factoryTask.isLeaf());
        
        // 4. Decorator - UrgentTaskDecorator
        UrgentTaskDecorator decorated = new UrgentTaskDecorator(builderTask);
        assertTrue(decorated.getTitle().contains("🚨 URGENTE:"));
        assertNotNull(decorated.getUrgencyMessage());
        
        // 5. Iterator - TaskIterator
        TaskIterator iterator = new TaskIterator(java.util.List.of(factoryTask, builderTask));
        assertTrue(iterator.hasNext());
        assertEquals(2, iterator.getTotalCount());
        
        // 6. Strategy - SortByTitle
        SortByTitle strategy = new SortByTitle();
        assertNotNull(strategy.getStrategyName());
        var sorted = strategy.sort(java.util.List.of(factoryTask, builderTask));
        assertEquals(2, sorted.size());
        
        // 7. Observer - tramite SimpleTaskManager
        taskManager.addTask(factoryTask);
        assertEquals(1, taskManager.getTaskCount());
        
        // 8. Proxy - SecureTaskManager (testato implicitamente sopra)
        assertNotNull(secureTaskManager.findTaskById(factoryTask.getId()));
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
        Task task = TaskFactory.createTask("Task Ricercabile");
        taskManager.addTask(task);
        
        Task found = taskManager.findTaskById(task.getId());
        
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
    @DisplayName("Dovrebbe eliminare task e verificare conteggio")
    void shouldDeleteTaskAndVerifyCount() {
        Task task = TaskFactory.createTask("Task da eliminare");
        taskManager.addTask(task);
        String taskId = task.getId();
        
        // Verifica che sia stato aggiunto
        assertEquals(1, taskManager.getTaskCount());
        assertNotNull(taskManager.findTaskById(taskId));
        
        boolean deleted = taskManager.deleteTask(taskId);
        
        assertTrue(deleted);
        assertEquals(0, taskManager.getTaskCount());
        assertNull(taskManager.findTaskById(taskId));
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
        Task task1 = TaskFactory.createTask("Task 1");
        Task task2 = TaskFactory.createTask("Task 2");
        Task task3 = TaskFactory.createTask("Task 3");
        
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        
        var allTasks = taskManager.getAllTasks();
        
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
    
    @Test
    @DisplayName("Dovrebbe gestire sicurezza del proxy")
    void shouldHandleProxySecurity() {
        // Test con utente valido
        SecureTaskManager validProxy = new SecureTaskManager(taskManager, "utente.valido");
        Task task = TaskFactory.createTask("Test Sicurezza");
        
        assertDoesNotThrow(() -> validProxy.addTask(task));
        assertEquals(1, taskManager.getTaskCount());
        
        // Test con utente non valido
        SecureTaskManager invalidProxy = new SecureTaskManager(taskManager, null);
        Task task2 = TaskFactory.createTask("Test Sicurezza 2");
        
        assertThrows(SecurityException.class, () -> invalidProxy.addTask(task2));
        // Il conteggio dovrebbe rimanere 1 (il secondo task non è stato aggiunto)
        assertEquals(1, taskManager.getTaskCount());
    }
}