package com.karangandhi.networking.core;

import java.io.IOException;
import java.util.UUID;

public abstract class Task {
    private boolean taskCompleted = false;
    private Context context;
    public boolean isAsynchronous;
    private Thread taskThread;
    
    public UUID ID;

    // TODO: fix the idle task
    public static class IDLE extends Task {
        public IDLE(Context context) {
            super(true, context);
        }

        @Override
        public void run() throws IOException {
            while (getContext().getTaskLength() == 0);
            try {
                getContext().start();
            } catch (TaskNotCompletedException e) {
                e.printStackTrace();
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
}
