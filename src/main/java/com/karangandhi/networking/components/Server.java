package com.karangandhi.networking.components;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Message;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;

// Server will be running on the main thread
public abstract class Server {
    private ArrayDeque<Message> readMessage;
    private ArrayDeque<Message> writeMessage;
    private ArrayList<Connection> clients;

    private String ip;
    private long port;
    private int type;
    private int backlog;

    private ServerSocket serverSocket;
    private InetAddress ipInetAddress;
    private Context serverContext;

    public boolean isRunning;

    // Constants
    public static final int TCP = 0;
    public static final int HTTP = 1;

    public Server(String ip, int port, int type, int backlog) throws IOException {
        this.ip = ip;
        this.port = port;
        this.isRunning = false;
        this.type = type;
        this.backlog = backlog;
        this.ipInetAddress = InetAddress.getByName(ip);
        this.serverSocket = new ServerSocket(port, backlog, this.ipInetAddress);
        this.serverContext = new Context();
    }

    public abstract boolean onClientConnected(Connection clientConnection);

    public abstract void onMessageReceived(Message receivedMessage, Connection client);

    public abstract boolean onClientDisConnected(Connection clientConnection);

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
        // TODO: start listening for clients forever until the stop function is called
        this.isRunning = true;
    }

    public void stop() {
        // Stop the context and all the ongoing tasks
        // TODO: stop the server
    }

    public ArrayList<Connection> getClients() {
        return clients;
    }

    public long getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
