package com.taskmanager.storage;

import com.taskmanager.models.Task;
import com.taskmanager.models.Project;
import com.taskmanager.models.TaskComponent;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe per salvare e caricare i dati.
 * Versione semplificata che salva in formato testo semplice.
 * Usa Collections thread-safe (Multithreading).
 */
public class TaskStorage {
    private final String fileName;
    // Mappa thread-safe per cache in memoria
    private final Map<String, TaskComponent> cache = new ConcurrentHashMap<>();
    
    /**
     * Costruttore
     * @param fileName nome del file per salvare i dati
     */
    public TaskStorage(String fileName) {
        this.fileName = fileName;
        
        // Crea la directory se non esiste
        try {
            Path dir = Paths.get("data");
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            System.err.println("⚠️  Errore creazione directory: " + e.getMessage());
        }
        
        loadFromFile();
    }
    
    /**
     * Salva un componente
     * @param component il componente da salvare
     */
    public void save(TaskComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Il componente non può essere null");
        }
        cache.put(component.getId(), component);
        saveToFile();
    }
    
    /**
     * Trova un componente per ID
     * @param id l'ID da cercare
     * @return il componente se trovato, null altrimenti
     */
    public TaskComponent findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return cache.get(id);
    }
    
    /**
     * Restituisce tutti i componenti
     * @return lista di tutti i componenti
     */
    public List<TaskComponent> findAll() {
        return new ArrayList<>(cache.values());
    }
    
    /**
     * Trova tutti i Task (usando Stream API)
     * @return lista di tutti i Task
     */
    public List<Task> findAllTasks() {
        return cache.values().stream()  // Stream API
                .filter(c -> c instanceof Task)  // Lambda expression
                .map(c -> (Task) c)  // Cast
                .toList();  // Java 16+ toList()
    }
    
    /**
     * Trova tutti i Project
     * @return lista di tutti i Project
     */
    public List<Project> findAllProjects() {
        return cache.values().stream()
                .filter(Project.class::isInstance)  // Method reference
                .map(Project.class::cast)
                .toList();
    }
    
    /**
     * Elimina un componente
     * @param id ID del componente da eliminare
     * @return true se eliminato, false se non trovato
     */
    public boolean delete(String id) {
        // Aggiunta validazione per parametri null/vuoti
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        
        boolean removed = cache.remove(id) != null;
        if (removed) {
            saveToFile();
        }
        return removed;
    }
    
    /**
     * Salva tutto su file di testo semplice (Java I/O)
     */
    private void saveToFile() {
        try {
            Path filePath = Paths.get("data", fileName.replace(".json", ".txt"));
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
                writer.println("# Task Manager Data File");
                writer.println("# Generato il: " + new Date());
                writer.println();
                
                for (TaskComponent component : cache.values()) {
                    if (component instanceof Task task) {
                        writer.printf("TASK|%s|%s|%s|%s|%s%n", 
                            task.getId(), 
                            task.getTitle().replace("|", "\\|"), 
                            task.getDescription() != null ? task.getDescription().replace("|", "\\|") : "",
                            task.getStatus(),
                            task.getPriority()
                        );
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Errore durante il salvataggio: " + e.getMessage());
        }
    }
    
    /**
     * Carica i dati dal file di testo
     */
    private void loadFromFile() {
        try {
            Path filePath = Paths.get("data", fileName.replace(".json", ".txt"));
            if (Files.exists(filePath)) {
                cache.clear();
                try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (!line.startsWith("#") && !line.trim().isEmpty()) {
                            parseLine(line);
                        }
                    }
                }
                System.out.println("✅ Dati caricati dal file: " + fileName);
            } else {
                System.out.println("📄 File non esistente, inizializzo vuoto: " + fileName);
            }
        } catch (Exception e) {
            System.err.println("⚠️  Errore durante il caricamento: " + e.getMessage());
        }
    }
    
    /**
     * Analizza una riga del file e crea l'oggetto corrispondente
     */
    private void parseLine(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 5 && "TASK".equals(parts[0])) {
                Task task = new Task(
                    parts[2].replace("\\|", "|"), // title
                    parts[3].replace("\\|", "|")  // description
                );
                // Imposta ID manualmente (normalmente non si farebbe)
                // task.setId(parts[1]); // Se avessi un setter per ID
                cache.put(parts[1], task);
            }
        } catch (Exception e) {
            System.err.println("⚠️  Errore parsing riga: " + line);
        }
    }
    
    /**
     * Restituisce il numero di elementi salvati
     * @return numero di elementi
     */
    public int size() {
        return cache.size();
    }

    /**
     * Stampa statistiche dello storage (per debugging)
     */
    public void printStats() {
        System.out.println("📊 STATISTICHE TASK STORAGE:");
        System.out.println("  Totale componenti: " + size());
        System.out.println("  Task: " + findAllTasks().size());
        System.out.println("  Progetti: " + findAllProjects().size());
        System.out.println("  File: " + fileName);
    }
    
    /**
     * Svuota la cache (per testing)
     */
    public void clear() {
        cache.clear();
    }
}