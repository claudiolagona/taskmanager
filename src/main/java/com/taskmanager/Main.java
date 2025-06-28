package com.taskmanager;

import com.taskmanager.cli.TaskManagerCLI;
import com.taskmanager.config.AppConfig;

/**
 * Classe principale dell'applicazione Task Manager.
 * Entry point del programma - il primo metodo che viene eseguito.
 */
public class Main {
    
    /**
     * Metodo main - punto di ingresso dell'applicazione
     * @param args argomenti passati da linea di comando
     */
    public static void main(String[] args) {
        System.out.println("🗂️  Task Manager CLI - Claudio La Gona");
        System.out.println("========================================");
        
        try {
            // Inizializza la configurazione (Singleton)
            AppConfig config = AppConfig.getInstance();
            config.init();
            
            // Avvia l'interfaccia CLI
            TaskManagerCLI cli = new TaskManagerCLI();
            cli.start();
            
        } catch (Exception e) {
            System.err.println("❌ Errore durante l'avvio: " + e.getMessage());
            System.exit(1);
        }
    }
}