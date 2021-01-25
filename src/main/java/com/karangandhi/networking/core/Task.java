package com.karangandhi.networking.core;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * The task that will be added to the context
 */
public abstract class Task {
    private boolean taskCompleted = false;
    private Context context;
    public boolean isAsynchronous;
    private Thread taskThread;
    
    public UUID ID;

    // TODO: fix the idle task
    public static class IDLE extends Task {
        public interface Callback {
            void run(TaskNotCompletedException e);
        }

        private Callback callback;

        public IDLE(Context context, Callback callback) {
            super(true, context);
            this.callback = callback;
        }

        @Override
        public void run() throws IOException {
            while (getContext().getTaskLength() == 0);
            try {
                getContext().start();
            } catch (TaskNotCompletedException e) {
                callback.run(e);
            }
        }

        @Override
        public boolean onComplete(Exception exception) {
            return true;
        }
    }

    public Task(boolean isAsynchronous, Context context) {
        ID = UUID.randomUUID();
        this.isAsynchronous = isAsynchronous;
        this.context = context;
        this.taskThread = null;
    }

    public abstract void run() throws IOException;

    public abstract boolean onComplete(Exception exception);

    // Optional method to override
    public void onInitialise() {}

    public void markCompleted() {
        taskCompleted = true;
    }

    public boolean isCompleted() {
        return taskCompleted;
    }

    public void markNotCompleted() {
        taskCompleted = false;
    }

    public Context getContext() {
        return context;
    }

    public void setTaskThread(Thread taskThread) {
        this.taskThread = taskThread;
    }

    public Thread getTaskThread() {
        return this.taskThread;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return isAsynchronous == task.isAsynchronous &&
                Objects.equals(ID, task.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskCompleted, context, isAsynchronous, taskThread, ID);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskCompleted=" + taskCompleted +
                ", isAsynchronous=" + isAsynchronous +
                ", taskThread=" + taskThread +
                ", ID=" + ID +
                '}';
    }
}
