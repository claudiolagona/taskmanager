package com.taskmanager.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per AppConfig - Pattern Singleton.
 */
@DisplayName("Test per AppConfig - Singleton Pattern")
class AppConfigTest {
    
    @Test
    @DisplayName("Dovrebbe restituire sempre la stessa istanza (Singleton)")
    void shouldReturnSameInstance() {
        // Act
        AppConfig instance1 = AppConfig.getInstance();
        AppConfig instance2 = AppConfig.getInstance();
        
        // Assert
        assertNotNull(instance1, "Prima istanza non dovrebbe essere null");
        assertNotNull(instance2, "Seconda istanza non dovrebbe essere null");
        assertSame(instance1, instance2, "Dovrebbe restituire la stessa istanza (Singleton)");
    }
    
    @Test
    @DisplayName("Dovrebbe inizializzare correttamente")
    void shouldInitializeCorrectly() {
        // Arrange
        AppConfig config = AppConfig.getInstance();
        
        // Act
        assertDoesNotThrow(() -> config.init(), "Inizializzazione non dovrebbe lanciare eccezioni");
        
        // Assert - dovrebbe avere proprietà di default
        assertNotNull(config.getProperty("app.name"));
        assertNotNull(config.getProperty("app.version"));
        assertNotNull(config.getProperty("storage.file"));
    }
    
    @Test
    @DisplayName("Dovrebbe gestire proprietà con valori di default")
    void shouldHandlePropertiesWithDefaults() {
        AppConfig config = AppConfig.getInstance();
        config.init();
        
        // Test proprietà esistente
        String appName = config.getProperty("app.name");
        assertNotNull(appName);
        
        // Test proprietà inesistente con default
        String customProp = config.getProperty("proprieta.inesistente", "valore.default");
        assertEquals("valore.default", customProp);
        
        // Test proprietà inesistente senza default
        String missingProp = config.getProperty("altra.proprieta.inesistente");
        assertNull(missingProp);
    }
    
    @Test
    @DisplayName("Dovrebbe essere thread-safe")
    void shouldBeThreadSafe() throws InterruptedException {
        // Test thread safety del Singleton
        AppConfig[] instances = new AppConfig[10];
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = AppConfig.getInstance();
            });
        }
        
        // Avvia tutti i thread
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Aspetta che finiscano
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verifica che tutte le istanze siano uguali
        AppConfig firstInstance = instances[0];
        for (int i = 1; i < instances.length; i++) {
            assertSame(firstInstance, instances[i], 
                      "Tutte le istanze dovrebbero essere uguali (thread safety)");
        }
    }
}