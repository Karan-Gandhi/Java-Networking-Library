package com.karangandhi.networking.core;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * The task that will be added to the context
 */
public abstract class Task {
    private boolean taskCompleted = false;
    final private Context context;
    public boolean isAsynchronous;
    private Thread taskThread;
    
    public UUID ID;

    /**
     * Creates a IDLE task that prevents the context from exiting
     */
    // TODO: fix the idle task
    public static class IDLE extends Task {
        /**
         * The callback which will be called when an error occurs while starting the context
         */
        public interface Callback {
            /**
             * @param e     The exception that caused the callback to be called
             */
            void run(TaskNotCompletedException e);
        }

        final private Callback callback;

        /**
         * Creates a instance of an IDLE task
         *
         * @param context       The context that the task is added to
         * @param callback      The callback which will be called when there is a exception while starting the context
         */
        public IDLE(Context context, Callback callback) {
            super(true, context);
            this.callback = callback;
        }

        /**
         * Starts the task
         *
         * @throws IOException      There is a exception that arises in the task
         */
        @Override
        public void run() throws IOException {
            while (getContext().getTaskLength() == 0);
            try {
                getContext().start();
            } catch (TaskNotCompletedException e) {
                callback.run(e);
            }
        }

        /**
         * Method that is called when the task is completed
         *
         * @param exception     The exception that arises while running the task
         * @return              Only true in this case
         */
        @Override
        public boolean onComplete(Exception exception) {
            return false;
        }
    }

    /**
     * Creates a instance of a Task
     *
     * @param isAsynchronous    If the task is Asynchronous
     * @param context           The context that the task belong to
     */
    public Task(boolean isAsynchronous, Context context) {
        ID = UUID.randomUUID();
        this.isAsynchronous = isAsynchronous;
        this.context = context;
        this.taskThread = null;
    }

    /**
     * Runs the task. It is a abstract method that is called to run the task
     *
     * @throws IOException      If an exception arises in the task
     */
    public abstract void run() throws IOException;

    /**
     * Abstract method that will be called when the task is completed
     *
     * @param exception     Exception that arises when completing the task
     * @return              True if there is no error and can complete the task else throws TaskNotCompletedException
     */
    public abstract boolean onComplete(Exception exception);

    /**
     * An optional method to override that will be called when the task is Initialised
     */
    public void onInitialise() {}

    /**
     * Mark the task as completed
     */
    public void markCompleted() {
        taskCompleted = true;
    }

    /**
     * @return      True if the task is completed
     */
    public boolean isCompleted() {
        return taskCompleted;
    }

    /**
     * Mark the task as not completed
     */
    public void markNotCompleted() {
        taskCompleted = false;
    }

    /**
     * @return      The context that the task belongs to
     */
    public Context getContext() {
        return context;
    }

    /**
     * Set the thread that the task is running om
     *
     * @param taskThread        The thread on which the task is running
     */
    public void setTaskThread(Thread taskThread) {
        this.taskThread = taskThread;
    }

    /**
     * Returns the task thread on which the task is running on
     *
     * @return      The task thread
     */
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
