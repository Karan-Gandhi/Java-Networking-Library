package com.karangandhi.networking.utils;

import com.karangandhi.networking.components.Connection;
import com.karangandhi.networking.components.Server;
import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.core.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Tasks {
    public static class ServerTasks {
        public static class ReadMessageTask extends Task {
            private InputStream inputStream;

            // TODO: add a handler
            public ReadMessageTask(Context context, InputStream inputStream) {
                super(true, context);
                this.inputStream = inputStream;
            }

            @Override
            public void run() throws IOException {
                while (true) {
                    Message newMessage = Message.readFrom(this.inputStream);
                }
            }

            @Override
            public boolean onComplete() {
                return false;
            }
        }
    }
}
