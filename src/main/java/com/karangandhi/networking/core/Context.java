package com.karangandhi.networking.core;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Context {
    private ArrayDeque<Task> tasks;
    private ArrayList<Thread> workers;
    private OnStartCallback onStartCallback;
    private boolean isRunning;

    public static interface OnStartCallback {
        void onStart();
    }

    public Context() {
        tasks = new ArrayDeque<>();
        workers = new ArrayList<>();
        onStartCallback = null;
        isRunning = false;
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    private Task getNextTask() {
        return tasks.removeFirst();
    }

    public void start() throws TaskNotCompletedException, IOException {
        isRunning = true;
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
            // if (tasks.isEmpty()) this.addTask(new Task.IDLE(this, (TaskNotCompletedException Ignored) -> { }));
            // while (tasks.isEmpty()) { }
        }
        isRunning = false;
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

    @SuppressWarnings("deprecation")
    public void stop() {
        for (Thread thread : workers) {
            if (thread.isAlive()) {
                try {
                    thread.interrupt();
                    thread.stop();
                } catch (Exception ignore) { }
            }
        }
        tasks.clear();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Task getFirstTask() {
        return this.tasks.getFirst();
    }

    public void addOnStartCallback(OnStartCallback callback) {
        this.onStartCallback = callback;
    }
}
