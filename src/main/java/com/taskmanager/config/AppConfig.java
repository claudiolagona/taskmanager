package com.taskmanager.config;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Configurazione dell'applicazione.
 * Implementa il pattern Singleton - può esistere una sola istanza.
 */
public class AppConfig {
    // L'unica istanza (Singleton)
    private static AppConfig instance;
    private Properties properties;
    private boolean initialized = false;
    
    // Costruttore privato per impedire istanziazione diretta
    private AppConfig() {
        properties = new Properties();
    }
    
    /**
     * Metodo per ottenere l'istanza singleton
     * @return l'unica istanza di AppConfig
     */
    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) { // Thread-safe
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }
    
    /**
     * Inizializza la configurazione caricando le proprietà
     */
    public void init() {
        if (!initialized) {
            loadProperties();
            initialized = true;
            System.out.println("✅ Configurazione inizializzata");
        }
    }
    
    /**
     * Carica le proprietà dal file (Java I/O)
     */
    private void loadProperties() {
        try (InputStream input = getClass().getResourceAsStream("/app.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                // Valori di default se il file non esiste
                setDefaultProperties();
            }
        } catch (IOException e) {
            System.err.println("⚠️  Errore caricamento configurazione, uso valori default");
            setDefaultProperties();
        }
    }
    
    /**
     * Imposta valori di configurazione di default
     */
    private void setDefaultProperties() {
        properties.setProperty("app.name", "Task Manager CLI");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("storage.file", "tasks.json");
    }
    
    /**
     * Ottiene una proprietà di configurazione
     * @param key la chiave della proprietà
     * @return il valore della proprietà
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Ottiene una proprietà con valore di default
     * @param key la chiave
     * @param defaultValue valore di default
     * @return il valore della proprietà o il default
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}