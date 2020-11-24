package com.karangandhi.networking.core;

import java.util.UUID;

public abstract class Task {
    private boolean taskCompleted = false;
    private Context context;
    public boolean isAsynchronous;
    
    public UUID ID;

    // TODO: fix the idle task
    public static class IDLE extends Task {
        public IDLE(Context context) {
            super(true, context);
        }

        @Override
        public void run() {
            while (getContext().getTaskLength() == 0);
            try {
                getContext().start();
            } catch (TaskNotCompletedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onComplete() {
            return true;
        }
    }

    public Task(boolean isAsynchronous, Context context) {
        ID = UUID.randomUUID();
        this.isAsynchronous = isAsynchronous;
        this.context = context;
    }

    public abstract void run();

    public abstract boolean onComplete();

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
}
