package com.karangandhi.networking.utils;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Task;

import java.io.IOException;
import java.io.InputStream;

/**
 * This contains all the tasks that will be used be the server and the client
 */
public class Tasks {
    /**
     * This is the readMessage task that is assigned by the connection to the Owner Context
     */
    public static class ReadMessageTask extends Task {
        /**
         * This is the callback that is called when a message appears on the input stream
         */
        public interface Callback {
            void onMessageReceived(Message<?, ?> message);
        }

        /**
         * This is the callback that is called when the server or the client closes the connection
         */
        public interface DisconnectCallback {
            void onDisconnect();
        }

        final private InputStream inputStream;
        final private Callback readMessageCallback;
        final private DisconnectCallback disconnect;
        public boolean isAlive;

        /**
         * Creates a instance of the ReadMessageTask
         *
         * @param context           The context of the Task
         * @param inputStream       The inputStream from which the messages must be read
         * @param callback          The callback that is called when a message is recieved
         * @param disconnect        The callback that is called when the server or the client closes the connection
         */
        public ReadMessageTask(Context context, InputStream inputStream, Callback callback, DisconnectCallback disconnect) {
            super(true, context);
            this.inputStream = inputStream;
            this.readMessageCallback = callback;
            this.disconnect = disconnect;
            this.isAlive = true;
        }

        /**
         * Runs the task
         *
         * @throws IOException      Thrown when there is a error reading the message
         */
        @Override
        public void run() throws IOException {
            while (isAlive) {
                synchronized (this.inputStream) {
                    Message<?, ?> newMessage = Message.readFrom(this.inputStream);
                    readMessageCallback.onMessageReceived(newMessage);
                }
            }
        }

        /**
         * This will colas the connection if there is a error while reading
         *
         * @param e     The error while reading
         * @return      True in all cases
         */
        @Override
        public boolean onComplete(Exception e) {
            if (e != null) disconnect.onDisconnect();
            return true;
        }
    }
}
