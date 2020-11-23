package com.karangandhi.networking.core;

public class TaskNotCompletedException extends Exception {
    private Task taskNotCompleted;

    public TaskNotCompletedException(Task t) throws Exception {
        taskNotCompleted = t;
        taskNotCompleted.markNotCompleted();
        throw new Exception("Unable to complete task: " + taskNotCompleted.ID);
    }
}
