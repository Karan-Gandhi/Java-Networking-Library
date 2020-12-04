package com.karangandhi.networking.components;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.core.OwnerObject;
import com.karangandhi.networking.core.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.UUID;

public class Connection<T extends OwnerObject> {
    public enum Owner { CLIENT, SERVER }
    public enum DefaultMessages { CONNECTED, DISCONNECTED, PING, AUTHORISATION }

    private ArrayDeque<Message> outMessageQueue;

    private Context context;
    private Owner owner;
    private Socket ownerSocket;
    private T ownerObject;
    private UUID id;

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

    public void connectToServer() throws IOException {
        if (owner == Owner.CLIENT && ownerObject instanceof Client) {
            Short authenticationToken = (Short) Message.readFrom(socketInputStream).messageBody;
            Short newToken = this.encode(authenticationToken);
            // TODO: Build the message and send it
        }
    }

    public void connectToClient() {
        if (owner == Owner.SERVER) {

        }
    }

    public void disconnectFromServer() {
        if (owner == Owner.CLIENT) {

        }
    }
    
    public void disconnectFromClient() {
        if (owner == Owner.SERVER) {

        }
    }
    
    public void writeMessage() {
        synchronized (outMessageQueue) {

        }
    }

    public void readMessage() {
        synchronized (socketInputStream) {

        }
    }

    public UUID getId() {
        return id;
    }

    public int getPort() {
        return ownerSocket.getPort();
    }

    private Short encode(Short token) {
        return 0;
    }
}
