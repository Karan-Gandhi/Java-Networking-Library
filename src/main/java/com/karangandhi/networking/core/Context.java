package com.karangandhi.networking.core;

import java.util.ArrayDeque;

public class Context {
    private ArrayDeque<Task> tasks;

    public Context() {
        tasks = new ArrayDeque<>();
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    private Task getNextTask() {
        return tasks.removeFirst();
    }

    public void start() throws Exception {
        while(!tasks.isEmpty()) {
            Task currentTask = this.getNextTask();
            if (!currentTask) {
                currentTask.run();
                currentTask.markCompleted();
                if (!currentTask.onComplete()) {
                    throw new TaskNotCompletedException(currentTask);
                }
            }
        }
    }
}
