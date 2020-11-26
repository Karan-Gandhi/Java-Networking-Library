package com.karangandhi.networking.components;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Message;

import java.net.Socket;
import java.util.ArrayDeque;
import java.util.UUID;

public class Connection<T> {
    public enum Owner { CLIENT, SERVER }
    public enum DefaultMessages { CONNECTED, DISCONNECTED }

    private Context context;
    private ArrayDeque<Message> outMessageQueue;
    private Owner owner;
    private Socket ownerSocket;
    private T ownerObject;
    private UUID id;

    public Connection(Context context, Owner owner, Socket socket, T ownerObject) {
        this.context = context;
        this.outMessageQueue = new ArrayDeque<Message>();
        this.owner = owner;
        this.ownerSocket = socket;
        this.ownerObject = ownerObject;
        this.id = UUID.randomUUID();
    }

    public void connectToServer() {

    }

    public void connectToClient() {

    }

    public void disconnectFromServer() {
        
    }
    
    public void disconnectFromClient() {
        
    }
    
    public void start() {
        // TODO: add readMessage to the Context
    }
    
    public void writeMessage() {
        
    }

    public void readMessage() {

    }

    public UUID getId() {
        return id;
    }
}
