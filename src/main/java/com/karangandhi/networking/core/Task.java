package com.karangandhi.networking.core;

import java.util.UUID;

public abstract class Task {
    private boolean taskCompleted = false;
    protected boolean isAsynchronous = false;

    public UUID ID;

    public static Task IDLE = new Task() {
        @Override
        public void run() {
            while (!isCompleted()) { }
        }

        @Override
        public boolean onComplete() {
            return false;
        }
    };

    public Task() {
        ID = UUID.randomUUID();
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

    public boolean isAsynchronous() {
        return isAsynchronous;
    }
}
