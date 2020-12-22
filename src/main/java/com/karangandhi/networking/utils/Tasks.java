package com.karangandhi.networking.utils;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Task;
import com.karangandhi.networking.core.Message;

import java.io.IOException;
import java.io.InputStream;

public class Tasks {
    public static class ServerTasks {
        public static class ReadMessageTask extends Task {
            public static interface Callback {
                void onMessageReceived(Message message);
            }

            private InputStream inputStream;
            private Callback readMessageCallback;
            public boolean isAlive;

            public ReadMessageTask(Context context, InputStream inputStream, Callback callback) {
                super(true, context);
                this.inputStream = inputStream;
                this.readMessageCallback = callback;
                this.isAlive = true;
            }

            @Override
            public void run() throws IOException {
                while (isAlive) {
                    System.out.println("[Server] Waiting for message!");
                    Message newMessage = Message.readFrom(this.inputStream);
                    readMessageCallback.onMessageReceived(newMessage);
                }
            }

            @Override
            public boolean onComplete(Exception e) {
                return e == null ? true : false;
            }
        }
    }
}
