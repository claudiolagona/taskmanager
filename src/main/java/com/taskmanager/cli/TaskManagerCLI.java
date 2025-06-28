package com.taskmanager.cli;

import com.taskmanager.adapter.LegacyTaskAdapter;
import com.taskmanager.builder.TaskBuilder;
import com.taskmanager.decorator.UrgentTaskDecorator;
import com.taskmanager.factory.TaskFactory;
import com.taskmanager.iterator.TaskIterator;
import com.taskmanager.models.Project;
import com.taskmanager.models.Task;
import com.taskmanager.models.TaskComponent;
import com.taskmanager.models.TaskPriority;
import com.taskmanager.models.TaskStatus;
import com.taskmanager.observer.ConsoleObserver;
import com.taskmanager.proxy.SecureTaskManager;
import com.taskmanager.services.SimpleTaskManager;
import com.taskmanager.services.TaskManager;
import com.taskmanager.strategy.SortByPriority;
import com.taskmanager.strategy.SortByStatus;
import com.taskmanager.strategy.SortByTitle;
import com.taskmanager.strategy.SortStrategy;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Interfaccia a linea di comando per il Task Manager.
 * Coordina tutti i pattern e fornisce un'interfaccia utente semplice.
 * 
 * @param taskManager istanza del TaskManager da utilizzare
 * @param scanner oggetto Scanner per leggere input da console
 * @param sortStrategy strategia di ordinamento per i task
 * @param running flag per controllare il loop principale
 */
public class TaskManagerCLI {
    private final TaskManager taskManager;
    private final Scanner scanner;
    private final SortStrategy<TaskComponent> sortStrategy;
    private boolean running = true;
    
    /**
     * Costruttore che inizializza tutti i componenti
     */
    public TaskManagerCLI() {
        // Crea il task manager base
        SimpleTaskManager simpleManager = new SimpleTaskManager();
        
        // Aggiunge un observer per le notifiche
        simpleManager.addObserver(new ConsoleObserver());
        
        // Wrappa con un proxy di sicurezza
        this.taskManager = new SecureTaskManager(simpleManager, "studente");
        
        this.scanner = new Scanner(System.in);
        this.sortStrategy = new SortByTitle();
        
        System.out.println("🚀 Task Manager avviato con successo!");
        System.out.println("Digita 'help' per vedere i comandi disponibili.");
    }
    
    /**
     * Avvia il loop principale della CLI
     */
    public void start() {
        while (running) {
            System.out.print("\ntaskmanager> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            String[] parts = input.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String args = parts.length > 1 ? parts[1] : "";
            
            try {
                processCommand(command, args);
            } catch (Exception e) {
                System.err.println("❌ Errore: " + e.getMessage());
            }
        }
    }
    
    /**
     * Processa i comandi dell'utente
     */
    private void processCommand(String command, String args) {
        switch (command) {
            case "help", "h" -> showHelp();
            case "add", "create" -> createTask(args);
            case "project", "proj" -> createProject(args);
            case "list", "ls" -> listTasks();
            case "show", "view" -> showTask(args);
            case "delete", "rm" -> deleteTask(args);
            case "urgent" -> createUrgentTask(args);
            case "status" -> changeTaskStatus(args);
            case "priority", "prio" -> changeTaskPriority(args);
            case "sort" -> sortTasks(args);
            case "filter" -> filterTasksByStatus(args);
            case "stats" -> showStatistics();
            case "export" -> exportToLegacy();
            case "demo" -> runDemo();
            case "quit", "exit", "q" -> quit();
            default -> System.out.println("❓ Comando sconosciuto: " + command + ". Digita 'help' per aiuto.");
        }
    }
    
    /**
     * Mostra l'aiuto
     */
    private void showHelp() {
        System.out.println("""
            
            📖 COMANDI DISPONIBILI:
            ==========================================
            🏗️  CREAZIONE:
            add <titolo>       - Crea un nuovo task
            urgent <titolo>    - Crea un task urgente  
            project <titolo>   - Crea un nuovo progetto
            
            📋 VISUALIZZAZIONE:
            list, ls          - Mostra tutti i task
            show <id>         - Mostra dettagli di un task
            sort [strategia]  - Ordina task (title/priority/status)
            filter [status]   - Filtra per status
            stats             - Mostra statistiche
            
            ✏️  MODIFICA:
            status <id>       - Cambia status di un task
            priority <id>     - Cambia priorità di un task
            delete <id>       - Elimina un task
            
            📤 UTILITÀ:
            export            - Esporta in formato legacy
            demo              - Esegue una demo dei pattern
            help, h           - Mostra questo aiuto
            quit, exit, q     - Esce dal programma
            
            Esempi:
            add Studiare Java
            project Sviluppo Website
            urgent Consegnare progetto
            status a1b2c3d4
            sort priority
            filter done
            """);
    }
    
    /**
     * Crea un nuovo task
     * 
     * @param title titolo del task
     */
    private void createTask(String title) {
        if (title.isEmpty()) {
            System.out.print("📝 Inserisci il titolo del task: ");
            title = scanner.nextLine().trim();
        }
        
        if (title.isEmpty()) {
            System.out.println("❌ Il titolo non può essere vuoto!");
            return;
        }
        
        Task task = new TaskBuilder(title)
                .withDescription("Task creato da CLI")
                .withPriority(TaskPriority.MEDIUM)
                .withStatus(TaskStatus.TODO)
                .build();
        
        taskManager.addTask(task);
    }
    
    /**
     * Crea un task urgente
     * 
     * @param title titolo del task urgente
     */
    private void createUrgentTask(String title) {
        if (title.isEmpty()) {
            System.out.print("🚨 Inserisci il titolo del task urgente: ");
            title = scanner.nextLine().trim();
        }
        
        if (title.isEmpty()) {
            System.out.println("❌ Il titolo non può essere vuoto!");
            return;
        }
        
        // Crea task
        Task task = TaskFactory.createTask(title, "Task urgente creato da CLI");
        task.setPriority(TaskPriority.CRITICAL);
        
        UrgentTaskDecorator urgentTask = new UrgentTaskDecorator(task);
        
        taskManager.addTask(task);
        System.out.println("⚡ " + urgentTask.getUrgencyMessage());
    }
    
    /**
     * Lista tutti i task
     */
    private void listTasks() {
        List<Task> tasks = taskManager.getAllTasks();
        
        if (tasks.isEmpty()) {
            System.out.println("📭 Nessun task presente.");
            return;
        }
        
        System.out.println("\n📋 LISTA TASK (" + tasks.size() + " totali):");
        System.out.println("=" .repeat(50));
        
        TaskIterator iterator = new TaskIterator(tasks);
        int counter = 1;
        
        while (iterator.hasNext()) {
            Task task = iterator.next();
            System.out.printf("%2d. [%s] %s - %s (%s)%n", 
                counter++, task.getId(), task.getTitle(), task.getStatus(), task.getPriority());
        }
        
        System.out.println("=" .repeat(50));
        System.out.println("💡 Usa 'show <id>' per vedere i dettagli di un task");
    }
    
    /**
     * Mostra i dettagli di un task specifico
     * 
     * @param id ID del task da visualizzare
     */
    private void showTask(String id) {
        if (id.isEmpty()) {
            System.out.print("🔍 Inserisci l'ID del task: ");
            id = scanner.nextLine().trim();
        }
        
        Task task = taskManager.findTaskById(id);
        if (task == null) {
            System.out.println("❌ Task non trovato con ID: " + id);
            return;
        }
        
        System.out.println("\n📋 DETTAGLI TASK:");
        System.out.println("=" .repeat(30));
        task.display(0);
        System.out.println("ID: " + task.getId());
        System.out.println("Creato: " + task.getCreatedAt());
        System.out.println("Aggiornato: " + task.getUpdatedAt());
    }
    
    /**
     * Elimina un task
     * 
     * @param id ID del task da eliminare
     */
    private void deleteTask(String id) {
        if (id.isEmpty()) {
            System.out.print("🗑️  Inserisci l'ID del task da eliminare: ");
            id = scanner.nextLine().trim();
        }
        
        boolean deleted = taskManager.deleteTask(id);
        if (!deleted) {
            System.out.println("❌ Task non trovato o errore durante l'eliminazione: " + id);
        }
    }

    /**
     * Crea un nuovo progetto
     * 
     * @param title titolo del progetto
     */
    private void createProject(String title) {
        if (title.isEmpty()) {
            System.out.print("📁 Inserisci il titolo del progetto: ");
            title = scanner.nextLine().trim();
        }
        
        if (title.isEmpty()) {
            System.out.println("❌ Il titolo non può essere vuoto!");
            return;
        }
        
        Project project = new Project(title, "Progetto creato da CLI");
        System.out.println("✅ Progetto creato: " + project.getTitle() + " (ID: " + project.getId() + ")");
        
        // Chiede se l'utente vuole aggiungere task al progetto
        System.out.print("Vuoi aggiungere task a questo progetto? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if ("y".equals(response) || "yes".equals(response)) {
            addTasksToProject(project);
        }
    }

    /**
     * Aggiunge task a un progetto
     * 
     * @param project il progetto a cui aggiungere i task
     */
    private void addTasksToProject(Project project) {
        System.out.println("📋 Aggiungi task al progetto '" + project.getTitle() + "'");
        System.out.println("(Inserisci 'done' per finire)");
        
        while (true) {
            System.out.print("Task title: ");
            String taskTitle = scanner.nextLine().trim();
            
            if ("done".equalsIgnoreCase(taskTitle)) {
                break;
            }
            
            if (!taskTitle.isEmpty()) {
                Task task = TaskFactory.createTask(taskTitle, "Task del progetto " + project.getTitle());
                project.addComponent(task);
                taskManager.addTask(task); // Salva anche nel sistema
                System.out.println("  ✅ Aggiunto: " + taskTitle);
            }
        }
        
        System.out.println("📁 Progetto completato con " + project.getComponents().size() + " task");
    }

    /**
     * Cambia lo status di un task
     * 
     * @param id ID del task da modificare
     */
    private void changeTaskStatus(String id) {
        if (id.isEmpty()) {
            System.out.print("🔄 Inserisci l'ID del task: ");
            id = scanner.nextLine().trim();
        }
        
        Task task = taskManager.findTaskById(id);
        if (task == null) {
            System.out.println("❌ Task non trovato: " + id);
            return;
        }
        
        System.out.println("📋 Task corrente: " + task.getTitle());
        System.out.println("Status attuale: " + task.getStatus());
        System.out.println("\nStatus disponibili:");
        System.out.println("1. TODO (Da fare)");
        System.out.println("2. IN_PROGRESS (In corso)");
        System.out.println("3. DONE (Completato)");
        System.out.println("4. CANCELLED (Annullato)");
        
        System.out.print("Scegli nuovo status (1-4): ");
        String choice = scanner.nextLine().trim();
        
        TaskStatus newStatus = switch (choice) {
            case "1" -> TaskStatus.TODO;
            case "2" -> TaskStatus.IN_PROGRESS;
            case "3" -> TaskStatus.DONE;
            case "4" -> TaskStatus.CANCELLED;
            default -> null;
        };
        
        if (newStatus != null) {
            task.setStatus(newStatus);
            System.out.println("✅ Status aggiornato: " + task.getTitle() + " → " + newStatus);
        } else {
            System.out.println("❌ Scelta non valida!");
        }
    }

    /**
     * Cambia la priorità di un task
     * 
     * @param id ID del task da modificare
     */
    private void changeTaskPriority(String id) {
        if (id.isEmpty()) {
            System.out.print("⚡ Inserisci l'ID del task: ");
            id = scanner.nextLine().trim();
        }
        
        Task task = taskManager.findTaskById(id);
        if (task == null) {
            System.out.println("❌ Task non trovato: " + id);
            return;
        }
        
        System.out.println("📋 Task corrente: " + task.getTitle());
        System.out.println("Priorità attuale: " + task.getPriority());
        System.out.println("\nPriorità disponibili:");
        System.out.println("1. LOW (Bassa)");
        System.out.println("2. MEDIUM (Media)");
        System.out.println("3. HIGH (Alta)");
        System.out.println("4. CRITICAL (Critica)");
        
        System.out.print("Scegli nuova priorità (1-4): ");
        String choice = scanner.nextLine().trim();
        
        TaskPriority newPriority = switch (choice) {
            case "1" -> TaskPriority.LOW;
            case "2" -> TaskPriority.MEDIUM;
            case "3" -> TaskPriority.HIGH;
            case "4" -> TaskPriority.CRITICAL;
            default -> null;
        };
        
        if (newPriority != null) {
            task.setPriority(newPriority);
            System.out.println("✅ Priorità aggiornata: " + task.getTitle() + " → " + newPriority);
        } else {
            System.out.println("❌ Scelta non valida!");
        }
    }

    /**
     * Ordina e mostra task con diverse strategie
     * 
     * @param strategy strategia di ordinamento (title, priority, status)
     */
    private void sortTasks(String strategy) {
        List<Task> tasks = taskManager.getAllTasks();
        
        if (tasks.isEmpty()) {
            System.out.println("📭 Nessun task da ordinare.");
            return;
        }
        
        if (strategy.isEmpty()) {
            System.out.println("📊 Strategie di ordinamento disponibili:");
            System.out.println("1. title - Ordina per titolo");
            System.out.println("2. priority - Ordina per priorità");
            System.out.println("3. status - Ordina per status");
            System.out.print("Scegli strategia: ");
            strategy = scanner.nextLine().trim().toLowerCase();
        }
        
        SortStrategy<TaskComponent> sortStrategy = switch (strategy) {
            case "title", "1" -> new SortByTitle();
            case "priority", "2" -> new SortByPriority();
            case "status", "3" -> new SortByStatus();
            default -> {
                System.out.println("❌ Strategia non riconosciuta, uso ordinamento per titolo");
                yield new SortByTitle();
            }
        };
        
        var sortedTasks = sortStrategy.sort(
            tasks.stream().map(t -> (TaskComponent) t).toList()
        );
        
        System.out.println("\n📋 TASK ORDINATI (" + sortStrategy.getStrategyName() + "):");
        System.out.println("=" .repeat(60));
        
        for (int i = 0; i < sortedTasks.size(); i++) {
            TaskComponent task = sortedTasks.get(i);
            if (task instanceof Task t) {
                System.out.printf("%2d. [%s] %s - %s (%s)%n", 
                    i + 1, t.getId(), t.getTitle(), t.getStatus(), t.getPriority());
            }
        }
    }

    /**
     * Filtra task per status
     * 
     * @param status lo status da filtrare (todo, progress, done, cancelled)
     */
    private void filterTasksByStatus(String status) {
        if (status.isEmpty()) {
            System.out.println("🔍 Filtra per status:");
            System.out.println("1. todo - Task da fare");
            System.out.println("2. progress - Task in corso");
            System.out.println("3. done - Task completati");
            System.out.println("4. cancelled - Task annullati");
            System.out.print("Scegli filtro: ");
            status = scanner.nextLine().trim().toLowerCase();
        }
        
        TaskStatus filterStatus = switch (status) {
            case "todo", "1" -> TaskStatus.TODO;
            case "progress", "2" -> TaskStatus.IN_PROGRESS;
            case "done", "3" -> TaskStatus.DONE;
            case "cancelled", "4" -> TaskStatus.CANCELLED;
            default -> null;
        };
        
        if (filterStatus == null) {
            System.out.println("❌ Status non valido!");
            return;
        }
        
        var filteredTasks = taskManager.getAllTasks().stream()
                .filter(task -> task.getStatus() == filterStatus)
                .toList();
        
        if (filteredTasks.isEmpty()) {
            System.out.println("📭 Nessun task con status: " + filterStatus);
            return;
        }
        
        System.out.println("\n🔍 TASK FILTRATI (Status: " + filterStatus + "):");
        System.out.println("=" .repeat(50));
        
        for (int i = 0; i < filteredTasks.size(); i++) {
            Task task = filteredTasks.get(i);
            System.out.printf("%2d. [%s] %s (%s)%n", 
                i + 1, task.getId(), task.getTitle(), task.getPriority());
        }
    }

    /**
     * Mostra statistiche dei task
     */
    private void showStatistics() {
        List<Task> allTasks = taskManager.getAllTasks();
        
        if (allTasks.isEmpty()) {
            System.out.println("📊 Nessun task presente per le statistiche.");
            return;
        }
        
        // Conta per status
        Map<TaskStatus, Long> statusCount = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
        
        // Conta per priorità
        Map<TaskPriority, Long> priorityCount = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));
        
        System.out.println("\n📊 STATISTICHE TASK:");
        System.out.println("=" .repeat(40));
        System.out.println("Totale task: " + allTasks.size());
        
        System.out.println("\n📈 Per Status:");
        statusCount.forEach((status, count) -> 
            System.out.printf("  %s: %d%n", status, count));
        
        System.out.println("\n⚡ Per Priorità:");
        priorityCount.forEach((priority, count) -> 
            System.out.printf("  %s: %d%n", priority, count));
        
        // Percentuale completamento
        long completedTasks = statusCount.getOrDefault(TaskStatus.DONE, 0L);
        double completionRate = (double) completedTasks / allTasks.size() * 100;
        System.out.printf("\n✅ Tasso di completamento: %.1f%%%n", completionRate);
    }

    /**
     * Esporta task in formato legacy
     */
    private void exportToLegacy() {
        List<Task> tasks = taskManager.getAllTasks();
        
        if (tasks.isEmpty()) {
            System.out.println("📭 Nessun task da esportare.");
            return;
        }
        
        LegacyTaskAdapter adapter = new LegacyTaskAdapter();
        
        System.out.println("\n📤 ESPORTAZIONE IN FORMATO LEGACY:");
        System.out.println("=" .repeat(50));
        
        for (Task task : tasks) {
            var legacyTask = adapter.adaptToLegacy(task);
            System.out.println(legacyTask);
        }
        
        System.out.println("\n✅ Esportati " + tasks.size() + " task in formato legacy");
    }
    
    /**
     * Esegue una demo di tutti i pattern implementati
     */
    private void runDemo() {
        System.out.println("\n🎯 DEMO DEI DESIGN PATTERNS");
        System.out.println("=" .repeat(40));
        
        // 1. Factory Pattern
        System.out.println("1️⃣  Factory Pattern - Creazione task:");
        Task factoryTask = TaskFactory.createTask("Task da Factory", "Creato con Factory Method");
        taskManager.addTask(factoryTask);
        
        // 2. Builder Pattern
        System.out.println("2️⃣  Builder Pattern - Task complesso:");
        Task builderTask = new TaskBuilder("Task da Builder")
                .withDescription("Creato con Builder Pattern")
                .withPriority(TaskPriority.HIGH)
                .withStatus(TaskStatus.IN_PROGRESS)
                .build();
        taskManager.addTask(builderTask);
        
        // 3. Decorator Pattern
        System.out.println("3️⃣  Decorator Pattern - Task urgente:");
        UrgentTaskDecorator decoratedTask = new UrgentTaskDecorator(factoryTask);
        decoratedTask.display(1);
        
        // 4. Strategy Pattern
        System.out.println("4️⃣  Strategy Pattern - Ordinamento:");
        List<Task> allTasks = taskManager.getAllTasks();
        var sortedTasks = sortStrategy.sort(allTasks.stream()
                .map(t -> (com.taskmanager.models.TaskComponent) t)
                .toList());
        System.out.println("Task ordinati per titolo: " + sortedTasks.size());
        
        // 5. Iterator Pattern
        System.out.println("5️⃣  Iterator Pattern - Iterazione:");
        TaskIterator iterator = new TaskIterator(allTasks);
        System.out.println("Iterando su " + iterator.getTotalCount() + " task...");
        
        System.out.println("✅ Demo completata! Tutti i pattern funzionano correttamente.");
    }
    
    /**
     * Esce dal programma
     */
    private void quit() {
        System.out.println("👋 Grazie per aver usato Task Manager CLI!");
        System.out.println("Arrivederci! 🎓");
        running = false;
        scanner.close();
    }
}