package com.karangandhi.networking.components;

import com.karangandhi.networking.core.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;
import java.util.ArrayList;

// Server will be running on another thread
public abstract class Server implements OwnerObject {
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
    private boolean verbose;

    // Constants
    public static final int TCP = 0;
    public static final int HTTP = 1;

    public Server(String ip, int port, int type, int backlog, boolean verbose) throws IOException {
        this.ip = ip;
        this.port = port;
        this.isRunning = false;
        this.type = type;
        this.backlog = backlog;
        this.ipInetAddress = InetAddress.getByName(ip);
        this.serverSocket = new ServerSocket(port, backlog, this.ipInetAddress);
        this.serverContext = new Context();
        this.verbose = verbose;
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
        // Running on another thread
        // TODO: start listening for clients forever until the stop function is called
        try {
            waitForClientConnection();
            serverContext.start();
            if (verbose) System.out.println("[Server] Server started successfully");
        } catch (TaskNotCompletedException e) {
            e.printStackTrace();
        }
    }

    private void waitForClientConnection() {
        serverContext.addTask(new Task(true, serverContext) {
            @Override
            public void run() {
                while(isRunning) {
                    try {
                        Socket socket = serverSocket.accept();
                        Connection clientConnection = onClientConnect(socket);
                        if (onClientConnected(clientConnection)) {
                            if (verbose) System.out.println("[Server] Client " + clientConnection.getId() + " successfully connected");
                            // TODO: Connection is successful
                        } else {
                            if (verbose) System.out.println("[Server] Client " + clientConnection.getId() + " rejected");
                            // TODO: Client rejected
                        }
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            }

            @Override
            public boolean onComplete() {
                return true;
            }
        });
    }

    private Connection onClientConnect(Socket clientSocket) throws IOException {
        Connection<Server> connection = new Connection(serverContext, Connection.Owner.SERVER, clientSocket, this);
        return null;
    }

    public void stop() {
        // Stop the context and all the ongoing tasks
        // TODO: stop the server
        isRunning = false;
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
