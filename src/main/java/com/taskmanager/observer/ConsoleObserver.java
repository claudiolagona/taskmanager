package com.taskmanager.observer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Observer concreto che stampa i messaggi sulla console.
 */
public class ConsoleObserver implements TaskObserver {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    @Override
    public void onTaskEvent(String eventType, String message, String taskId) {
        String timestamp = LocalTime.now().format(TIME_FORMAT);
        System.out.printf("🔔 [%s] %s: %s (ID: %s)%n", timestamp, eventType, message, taskId);
    }
}