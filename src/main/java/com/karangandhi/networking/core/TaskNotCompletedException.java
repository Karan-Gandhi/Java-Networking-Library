package com.karangandhi.networking.core;

/**
 * Exception that will be thrown when there is a error completing the task
 */
public class TaskNotCompletedException extends Exception {

    /**
     * Creates a instance of the task that is not completed
     * @param t     The tas where there is a error
     */
    public TaskNotCompletedException(Task t) {
        super("Unable to complete task: " + t.ID);
        t.markNotCompleted();
    }
}
