package com.karangandhi.networking.utils;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Task;

import java.io.IOException;
import java.io.InputStream;

public class Tasks {
    public static class ReadMessageTask extends Task {
        public static interface Callback {
            void onMessageReceived(Message<?, ?> message);
        }

        public static interface DisconnectCallback {
            void onDisconnect();
        }

        private InputStream inputStream;
        private Callback readMessageCallback;
        private DisconnectCallback disconnect;
        public boolean isAlive;

        public ReadMessageTask(Context context, InputStream inputStream, Callback callback, DisconnectCallback disconnect) {
            super(true, context);
            this.inputStream = inputStream;
            this.readMessageCallback = callback;
            this.disconnect = disconnect;
            this.isAlive = true;
        }

        @Override
        public void run() throws IOException {
            while (isAlive) {
                synchronized (this.inputStream) {
                    Message newMessage = Message.readFrom(this.inputStream);
                    readMessageCallback.onMessageReceived(newMessage);
                }
            }
        }

        @Override
        public boolean onComplete(Exception e) {
            if (e != null) disconnect.onDisconnect();
            return false;
        }
    }
}
