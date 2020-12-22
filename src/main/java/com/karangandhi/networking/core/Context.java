package com.karangandhi.networking.core;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

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

    public void start() throws TaskNotCompletedException, IOException {
        if (onStartCallback != null) this.onStartCallback.onStart();
        while(!tasks.isEmpty()) {
            Task currentTask = this.getNextTask();
            if (!currentTask.isAsynchronous) {
                Exception exception = null;
                try {
                    currentTask.onInitialise();
                    currentTask.run();
                } catch (Exception e) {
                    exception = e;
                }
                currentTask.markCompleted();
                if (!currentTask.onComplete(exception)) {
                    throw new TaskNotCompletedException(currentTask);
                }
            } else {
                Thread thread = new Thread(() -> {
                    synchronized (currentTask) {
                        Exception exception = null;
                        try {
                            currentTask.run();
                        } catch (IOException ioException) {
                            exception = ioException;
                        }
                        currentTask.markCompleted();
                        if (!currentTask.onComplete(exception)) try {
                            throw new TaskNotCompletedException(currentTask);
                        } catch (TaskNotCompletedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                currentTask.setTaskThread(thread);
                workers.add(thread);
                currentTask.onInitialise();
                thread.start();
            }
        }
    }

    public int getTaskLength() {
        return tasks.size();
    }

    public void pause() throws InterruptedException {
        // TODO: Test this method
        for (Thread thread : workers) {
            if(thread.isAlive()) thread.wait();
        }
    }

    public void resume() {
        // TODO: Test this method
        for (Thread thread : workers) {
            thread.notify();
        }
    }

    public void stop() {
        // TODO: Complete the stop method
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
