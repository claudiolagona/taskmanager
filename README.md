# Task Manager

## 1. Application Overview and Functionality

The Task Manager is a console-based Java application that allows users to create, list, update, delete, and sort tasks and projects. Each task can have a title, description, status (TODO, IN_PROGRESS, DONE, CANCELLED), and priority level. Projects can contain multiple tasks (and nested sub-projects), illustrating a hierarchical structure. All operations are performed via a Command-Line Interface (CLI), with real-time notifications and basic security checks.

**Key features:**
- **Task & Project Management:** Create standalone tasks or grouped projects, update their attributes, and delete them.  
- **Sorting & Iteration:** View tasks sorted by title, priority, or status using interchangeable strategies, and traverse them with a custom iterator.  
- **Design Patterns Demonstration:** Showcases multiple classic design patterns in action.  
- **Persistence & Notifications:** Simple file-based storage persists data between runs, while observers receive notifications on CRUD events.  

## 2. Technologies and Patterns Used

- **Java (JDK 11+):** Core programming language for portability and strong typing.  
- **Plain-Text File Storage:** Simplified persistence layer using thread-safe collections and plain text files (no external DB).  
- **Command-Line Interface:** Lightweight, no additional UI frameworks.  

### Design Patterns

| Pattern        | Components                             | Justification                                                                                   |
| -------------- | -------------------------------------- | ----------------------------------------------------------------------------------------------- |
| Singleton      | `AppConfig`                            | Ensures a single configuration instance manages application settings.                           |
| Factory Method | `TaskFactory`                          | Centralizes creation of `Task` and `Project` objects, simplifying object instantiation.         |
| Builder        | `TaskBuilder`                          | Constructs `Task` objects step-by-step with optional parameters, improving readability.         |
| Adapter        | `LegacyTaskAdapter`                    | Transforms modern `Task` objects to legacy format for compatibility with old systems.           |
| Composite      | `TaskComponent`, `Task`, `Project`     | Treats individual tasks and project groups uniformly via a common interface.                    |
| Iterator       | `TaskIterator`                         | Provides a standard way to traverse collections of tasks without exposing underlying structure. |
| Strategy       | `SortStrategy`, `SortByTitle`, etc.    | Enables dynamic swapping of sorting algorithms at runtime.                                      |
| Observer       | `TaskNotifier`, `ConsoleObserver`      | Decouples event generation from handling, notifying observers on task events.                   |
| Decorator      | `TaskDecorator`, `UrgentTaskDecorator` | Dynamically adds additional behavior (e.g., urgency messages) to existing `Task` objects.       |
| Proxy          | `SecureTaskManager`                    | Controls access to `TaskManager` operations, enforcing simple security rules.                   |

## 3. Setup and Execution Instructions

1. **Prerequisites**
   - Java Development Kit (JDK) 11 or higher installed.  
   - A terminal/console application.  

2. **Compile the Application**
   ```bash
   # Navigate to the project root (where the src directory resides)
   cd taskmanager/taskmanager

   # Create a directory for compiled classes
   mkdir -p bin

   # Compile all Java source files
   javac -d bin src/main/java/com/taskmanager/**/*.java

3. **Run the Application**
    ```bash
    # From the project root
    java -cp bin com.taskmanager.Main

4. **Using the CLI**
    - Type help to view available commands.
    - Follow prompts to create tasks/projects, list them, update attributes, sort, and delete.
    - Type exit or quit to terminate the application.

## 4. UML Diagrams

# 4.1 Class Diagram

@startuml
package com.taskmanager {
  interface TaskComponent {
    + getId()
    + getTitle()
    + setTitle(title)
    + display(indent)
    + isLeaf()
    + getStatus()
    + setStatus(status)
    + getPriority()
    + setPriority(priority)
  }

  class Task {
    - id: String
    - title: String
    - description: String
    - status: TaskStatus
    - priority: TaskPriority
    + getId()
    + getTitle()
    + setTitle()
    + ...
  }

  class Project {
    - id: String
    - title: String
    - description: String
    - components: List<TaskComponent>
    + add(component)
    + remove(component)
    + display(indent)
    + ...
  }

  TaskComponent <|-- Task
  TaskComponent <|-- Project

  class TaskBuilder
  class TaskFactory
  class LegacyTaskAdapter
  class TaskIterator
  interface SortStrategy
  class SortByTitle
  class SortByPriority
  class SortByStatus
  class TaskNotifier
  interface TaskObserver
  class ConsoleObserver
  class SecureTaskManager
  interface TaskManager
  class SimpleTaskManager
  class TaskStorage
  class AppConfig

  TaskBuilder --> Task
  TaskFactory --> TaskComponent
  LegacyTaskAdapter --> Task
  TaskIterator --> Task
  SortByTitle ..|> SortStrategy
  SortByPriority ..|> SortStrategy
  SortByStatus ..|> SortStrategy
  TaskNotifier --> TaskObserver
  ConsoleObserver ..|> TaskObserver
  SecureTaskManager ..|> TaskManager
  SimpleTaskManager ..|> TaskManager
  SimpleTaskManager --> TaskStorage
  SimpleTaskManager --> TaskNotifier
  AppConfig --> *
}
@enduml

# 4.2 Architectural Diagram

@startuml
actor User

rectangle "CLI Layer" {
  component TaskManagerCLI
}

rectangle "Security Layer" {
  component SecureTaskManager <<Proxy>>
}

rectangle "Service Layer" {
  component SimpleTaskManager <<Service>>
}

rectangle "Infrastructure Layer" {
  component TaskStorage <<Persistence>>
  component TaskNotifier <<Messaging>>
}

User -> TaskManagerCLI : Enter commands
TaskManagerCLI -> SecureTaskManager : perform operation()
SecureTaskManager -> SimpleTaskManager : forward if authorized
SimpleTaskManager -> TaskStorage : read/write tasks
SimpleTaskManager -> TaskNotifier : notify observers
TaskNotifier -> ConsoleObserver : onTaskEvent()
@enduml

## 5. Known Limitations and Future Work

- **Persistence:** Currently uses simple text files; consider integrating an embedded (H2) or external relational database for robustness.
- **Concurrency:** Limited thread safety via basic collections; advanced locking or transaction management could be added for multi-user scenarios.
- **User Interface:** CLI-only; develop a GUI or web-based frontend (e.g., using Spring Boot + React) for better usability.
- **Error Handling:** Minimal validation and error messages; introduce more comprehensive input validation and exception management.
- **Extensibility:** Add RESTful APIs for integration with other systems, and support for task dependencies and scheduling features.
- **Test Coverage:** Increase unit and integration tests for all components; adopt a continuous integration pipeline.