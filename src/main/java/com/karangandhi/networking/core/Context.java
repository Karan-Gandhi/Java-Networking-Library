package com.karangandhi.networking.core;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class Context {
    private ArrayDeque<Task> tasks;
    private ArrayList<Thread> workers;

    public Context() {
        tasks = new ArrayDeque<>();
        workers = new ArrayList<>();
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    private Task getNextTask() {
        return tasks.removeFirst();
    }

    public void start() throws TaskNotCompletedException {
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
}
