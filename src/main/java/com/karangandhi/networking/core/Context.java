package com.karangandhi.networking.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.UUID;

public class Context {
    private ArrayDeque<Task> tasks;
    private ArrayList<Thread> workers;
    private OnStartCallback onStartCallback;

    public Context() {
        tasks = new ArrayDeque<>();
        workers = new ArrayList<>();
        onStartCallback = null;
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    private Task getNextTask() {
        return tasks.removeFirst();
    }

    public void start() throws TaskNotCompletedException {
        if (onStartCallback != null) this.onStartCallback.onStart();
        while(!tasks.isEmpty()) {
            Task currentTask = this.getNextTask();
            if (!currentTask.isAsynchronous) {
                currentTask.run();

                currentTask.markCompleted();
                if (!currentTask.onComplete()) {
                    throw new TaskNotCompletedException(currentTask);
                }
            } else {
                Thread thread = new Thread(() -> {
                    synchronized (currentTask) {
                        currentTask.run();
                        currentTask.markCompleted();
                        if (!currentTask.onComplete()) try {
                            throw new TaskNotCompletedException(currentTask);
                        } catch (TaskNotCompletedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                currentTask.setTaskThread(thread);
                workers.add(thread);
            }
        }
    }

    public int getTaskLength() {
        return tasks.size();
    }

    public void pause() throws InterruptedException {
        for (Thread thread : workers) {
            if(thread.isAlive()) thread.wait();
        }
    }

    public void resume() {
        for (Thread thread : workers) {
            thread.notify();
        }
    }

    public void stop() {
        for (Thread thread : workers) {
//            if (thread.isAlive()) thread.stop();
        }
    }

    public Task getFirstTask() {
        return this.tasks.getFirst();
    }

    public void addOnStartCallback(OnStartCallback callback) {
        this.onStartCallback = callback;
    }

    public static interface OnStartCallback {
        void onStart();
    }
}
