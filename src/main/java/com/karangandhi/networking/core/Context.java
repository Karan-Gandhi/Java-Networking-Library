package com.karangandhi.networking.core;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This is a class that creates a context. This class will be responsible of all the
 * threads, tasks that will be running in the background
 */
@SuppressWarnings("unused")
public class Context {
    final private ArrayDeque<Task> tasks;
    final private ArrayList<Thread> workers;
    final private ArrayList<Task> activeTasks;
    private OnStartCallback onStartCallback;
    private boolean isRunning;

    public boolean isPaused = false;

    /**
     * This is a callback which will be called when the context starts
     */
    public interface OnStartCallback {
        void onStart();
    }

    /**
     * Creates a instance of Context
     */
    public Context() {
        activeTasks = new ArrayList<>();
        tasks = new ArrayDeque<>();
        workers = new ArrayList<>();
        onStartCallback = null;
        isRunning = false;
    }

    /**
     * Adds a task to the Context
     *
     * @param t     The task to be added
     */
    public void addTask(Task t) {
        tasks.add(t);
        if (this.isPaused) this.resume();
    }

    /**
     * This method return the next task that needs to be executed
     *
     * @return      The next task in the queue
     */
    private Task getNextTask() {
        return tasks.removeFirst();
    }

    /**
     * Starts the context.
     *
     * This method will assign all the threads to the asynchronous tasks and call the onInitialise and
     * the onComplete method of the task
     *
     * @throws TaskNotCompletedException    This is thrown when there is a exception that arises while completing the task
     */
    public void start() throws TaskNotCompletedException {
        isRunning = true;
        if (onStartCallback != null) this.onStartCallback.onStart();
        while(!tasks.isEmpty()) {
            Task currentTask = this.getNextTask();
            if (!currentTask.isAsynchronous) {
                Exception exception = null;
                try {
                    activeTasks.add(currentTask);
                    currentTask.onInitialise();
                    currentTask.run();
                } catch (Exception e) {
                    exception = e;
                }
                currentTask.markCompleted();
                activeTasks.remove(currentTask);
                if (!currentTask.onComplete(exception)) {
                    throw new TaskNotCompletedException(currentTask);
                }
            } else {
                activeTasks.add(currentTask);
                Thread thread = new Thread(() -> {
                    synchronized (currentTask) {
                        Exception exception = null;
                        try {
                            currentTask.run();
                            currentTask.markCompleted();
                        } catch (IOException ioException) {
                            exception = ioException;
                        }
                        if (!currentTask.onComplete(exception)) try {
                            throw new TaskNotCompletedException(currentTask);
                        } catch (TaskNotCompletedException e) {
                            e.printStackTrace();
                        } finally {
                            activeTasks.remove(currentTask);
                        }
                    }
                });
                currentTask.setTaskThread(thread);
                workers.add(thread);
                currentTask.onInitialise();
                thread.start();
            }
        }
        try {
            this.pause();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
//        isRunning = false;
    }

    /**
     * Get the length of the task queue
     *
     * @return      The length of the remaining tasks
     */
    public int getTaskLength() {
        return tasks.size();
    }

    /**
     * Pauses the context
     *
     * @throws InterruptedException     This is thrown when any thread is interrupted
     */
    public void pause() throws InterruptedException {
        // TODO: Test this method
        this.isPaused = true;
        for (Thread thread : workers) {
            if(thread.isAlive()) thread.wait();
        }
    }

    /**
     * Resumes the context if it is paused
     */
    public void resume() {
        // TODO: Test this method
        this.isPaused = false;
        for (Thread thread : workers) {
            thread.notify();
        }
    }

    /**
     * Stops the context killing all the threads
     */
    @SuppressWarnings("deprecation")
    public void stop() {
        isRunning = false;
        for (Thread thread : workers) {
            if (thread.isAlive()) {
                try {
                    thread.wait();
                    thread.stop();
                } catch (Exception ignore) { }
            }
        }
        tasks.clear();
    }

    /**
     * Returns the state of the context: if it is running or not
     *
     * @return      true if the context is running
     */
    public boolean isRunning() {
        return !isRunning;
    }

    /**
     * Gets the first task in the queue
     *
     * @return      The first task in the queue
     */
    public Task getFirstTask() {
        return this.tasks.getFirst();
    }

    /**
     * Adds a callback that will be called when the context starts
     *
     * @param callback      The callback to be added
     */
    public void addOnStartCallback(OnStartCallback callback) {
        this.onStartCallback = callback;
    }

    /**
     * Fetches all the active tasks
     *
     * @return      All the active tasks of the context
     */
    public ArrayList<Task> getActiveTasks() {
        return activeTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Context context = (Context) o;
        return Objects.equals(tasks, context.tasks) &&
                Objects.equals(workers, context.workers) &&
                Objects.equals(activeTasks, context.activeTasks) &&
                Objects.equals(onStartCallback, context.onStartCallback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, workers, activeTasks, onStartCallback, isRunning);
    }

    @Override
    public String toString() {
        return "Context{" +
                "tasks=" + tasks +
                ", workers=" + workers +
                ", activeTasks=" + activeTasks +
                ", onStartCallback=" + onStartCallback +
                ", isRunning=" + isRunning +
                '}';
    }
}
