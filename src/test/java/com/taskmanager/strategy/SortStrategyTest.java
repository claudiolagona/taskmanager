package com.taskmanager.strategy;

import com.taskmanager.models.Task;
import com.taskmanager.models.TaskComponent;
import com.taskmanager.models.TaskPriority;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Test per le strategie di ordinamento - Pattern Strategy.
 */
@DisplayName("Test per Strategy Pattern - Ordinamento")
class SortStrategyTest {
    
    private Task taskA;
    private Task taskB;
    private Task taskZ;
    private List<TaskComponent> taskList;
    
    @BeforeEach
    void setUp() {
        taskA = new Task("A Task", "Prima task alfabeticamente");
        taskB = new Task("B Task", "Seconda task alfabeticamente");
        taskZ = new Task("Z Task", "Ultima task alfabeticamente");
        
        // Imposta priorità diverse per test
        taskA.setPriority(TaskPriority.LOW);
        taskB.setPriority(TaskPriority.HIGH);
        taskZ.setPriority(TaskPriority.MEDIUM);
        
        taskList = Arrays.asList(taskZ, taskA, taskB); // Lista non ordinata
    }
    
    @Test
    @DisplayName("SortByTitle dovrebbe ordinare alfabeticamente")
    void sortByTitleShouldSortAlphabetically() {
        // Test Strategy Pattern con ordinamento per titolo
        SortStrategy<TaskComponent> strategy = new SortByTitle();
        
        List<TaskComponent> sorted = strategy.sort(taskList);
        
        assertEquals(3, sorted.size(), "Lista ordinata dovrebbe avere 3 elementi");
        assertEquals("A Task", sorted.get(0).getTitle(), "Primo dovrebbe essere A Task");
        assertEquals("B Task", sorted.get(1).getTitle(), "Secondo dovrebbe essere B Task");
        assertEquals("Z Task", sorted.get(2).getTitle(), "Terzo dovrebbe essere Z Task");
        
        // Verifica che lista originale non sia modificata
        assertEquals("Z Task", taskList.get(0).getTitle(), "Lista originale non dovrebbe essere modificata");
    }
    
    @Test
    @DisplayName("Strategy dovrebbe gestire liste vuote")
    void strategyShouldHandleEmptyLists() {
        SortStrategy<TaskComponent> strategy = new SortByTitle();
        
        List<TaskComponent> emptyResult = strategy.sort(new ArrayList<>());
        List<TaskComponent> nullResult = strategy.sort(null);
        
        assertTrue(emptyResult.isEmpty(), "Lista vuota dovrebbe rimanere vuota");
        assertTrue(nullResult.isEmpty(), "Lista null dovrebbe restituire lista vuota");
    }
    
    @Test
    @DisplayName("Strategy dovrebbe avere nome descrittivo")
    void strategyShouldHaveDescriptiveName() {
        SortStrategy<TaskComponent> strategy = new SortByTitle();
        
        String strategyName = strategy.getStrategyName();
        
        assertNotNull(strategyName, "Nome strategia non dovrebbe essere null");
        assertFalse(strategyName.trim().isEmpty(), "Nome strategia non dovrebbe essere vuoto");
        assertTrue(strategyName.toLowerCase().contains("title") || 
                  strategyName.toLowerCase().contains("titolo"), 
                  "Nome dovrebbe indicare il tipo di ordinamento");
    }
    
    @Test
    @DisplayName("Diverse strategie dovrebbero produrre ordinamenti diversi")
    void differentStrategiesShouldProduceDifferentSorting() {
        // Test che dimostra l'intercambiabilità delle strategie (Strategy Pattern)
        SortStrategy<TaskComponent> titleStrategy = new SortByTitle();
        
        List<TaskComponent> byTitle = titleStrategy.sort(taskList);
        
        // L'ordinamento per titolo dovrebbe essere diverso da quello originale
        assertNotEquals(taskList.get(0).getTitle(), byTitle.get(0).getTitle(),
                       "Ordinamento dovrebbe cambiare l'ordine");
    }
    
    @Test
    @DisplayName("Strategy dovrebbe essere riutilizzabile")
    void strategyShouldBeReusable() {
        // Test riutilizzo della stessa strategia
        SortStrategy<TaskComponent> strategy = new SortByTitle();
        
        List<TaskComponent> result1 = strategy.sort(taskList);
        List<TaskComponent> result2 = strategy.sort(taskList);
        
        assertEquals(result1.size(), result2.size(), "Risultati dovrebbero essere consistenti");
        for (int i = 0; i < result1.size(); i++) {
            assertEquals(result1.get(i).getTitle(), result2.get(i).getTitle(),
                        "Ordinamenti multipli dovrebbero dare stesso risultato");
        }
    }
}