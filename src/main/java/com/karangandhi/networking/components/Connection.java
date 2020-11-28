package com.karangandhi.networking.components;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.core.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.UUID;

public class Connection<T> {
    public enum Owner { CLIENT, SERVER }
    public enum DefaultMessages { CONNECTED, DISCONNECTED, PING }

    private Context context;
    private ArrayDeque<Message> outMessageQueue;
    private Owner owner;
    private Socket ownerSocket;
    private T ownerObject;
    private UUID id;
    private Message<DefaultMessages, Serializable> defaultMessagesSerializableMessage;

    private InputStream socketInputStream;
    private OutputStream socketOutputStream;

    public Connection(Context context, Owner owner, Socket socket, T ownerObject) throws IOException {
        this.context = context;
        this.outMessageQueue = new ArrayDeque<Message>();
        this.owner = owner;
        this.ownerSocket = socket;
        this.ownerObject = ownerObject;
        this.id = UUID.randomUUID();
        this.socketInputStream = socket.getInputStream();
        this.socketOutputStream = socket.getOutputStream();
    }

    public void connectToServer() {
        if (owner == Owner.CLIENT) {
            // add a async task to just read messages in the messages in the in message queue
            context.addTask(new READ_MESSAGE_TASK(context));
            context.addTask(new WRITE_MESSAGE_TASK(context));
        }
    }

    public void connectToClient() {
        if (owner == Owner.SERVER) {
            context.addTask(new READ_MESSAGE_TASK(context));
            context.addTask(new WRITE_MESSAGE_TASK(context));
        }
    }

    public void disconnectFromServer() {
        if (owner == Owner.CLIENT) {
            outMessageQueue.add(new Message(DefaultMessages.DISCONNECTED, null));
        }
    }
    
    public void disconnectFromClient() {
        if (owner == Owner.SERVER) {
            outMessageQueue.add(new Message(DefaultMessages.DISCONNECTED, null));
        }
    }
    
    public void writeMessage() {
        while(!outMessageQueue.isEmpty()) {
            Message currentMessage = outMessageQueue.removeFirst();
            try {
                currentMessage.writeTo(this.socketOutputStream);
            } catch (IOException exception) {
                // The Client is disconnected
            }
        }
    }

    public void readMessage() {

    }

    public UUID getId() {
        return id;
    }

    private class READ_MESSAGE_TASK extends Task {
        // TODO: optimise the thread to sleep and wakeup only when a message is added
        public READ_MESSAGE_TASK(Context context) {
            super(true, context);
        }

        @Override
        public void run() {
            outMessageQueue.add(new Message(DefaultMessages.CONNECTED, null));
            while (true) readMessage();
        }

        @Override
        public boolean onComplete() {
            return true;
        }
    }

    private class WRITE_MESSAGE_TASK extends Task {
        // TODO: optimise the thread to sleep and wakeup only when a message is added
        public WRITE_MESSAGE_TASK(Context context) {
            super(true, context);
        }

        @Override
        public void run() {
            while(true) writeMessage();
        }

        @Override
        public boolean onComplete() {
            return true;
        }
    }
}
