package com.karangandhi.networking.core;

public class TaskNotCompletedException extends Exception {
    private Task taskNotCompleted;

    public TaskNotCompletedException(Task t) {
        super("Unable to complete task: " + t.ID);
        taskNotCompleted = t;
        taskNotCompleted.markNotCompleted();
    }
}
