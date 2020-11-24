package com.karangandhi.networking.components;

import com.karangandhi.networking.core.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;

// Server will be running on the main thread
public abstract class Server {
    private ArrayDeque<Message> readMessage;
    private ArrayDeque<Message> writeMessage;
    private ArrayList<Connection> clients;
    public boolean isRunning;
    private String ip;
    private long port;

    public Server(String ip, long port) {
        this.ip = ip;
        this.port = port;
        this.isRunning = false;
    }

    public abstract void onStart();
    public abstract void onMessageReceived(Message receivedMessage, Connection client);
    public abstract void onEnd();

    public void sendMessage(Message message, Connection client) {
        // TODO: send the message to the client
    }
    
    public void sendAll(Message message) {
        // TODO: send the message to all clients
    }
    
    public void sendAll(Message message, Connection excludeClient) {
        // TODO: send message to all clients except the excludedClient
    }

    public void start() {
        // Running on the main thread
        // TODO: start listening for clients forever
    }
}
