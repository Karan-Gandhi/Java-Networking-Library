package com.karangandhi.networking.components;

import com.karangandhi.networking.core.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;
import java.util.ArrayList;

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
    private Thread serverThread;

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

    public void start() throws IOException, TaskNotCompletedException {
        // Running on another thread
        // TODO: start listening for clients forever until the stop function is called
        isRunning = true;
        waitForClientConnection();
        serverContext.start();
        if (verbose) System.out.println("[Server] Server started successfully");
    }

    private void waitForClientConnection() throws IOException {
        Task serverTask = new Task(true, serverContext) {
            @Override
            public void run() throws IOException {
                while(isRunning) {
                    Socket socket = serverSocket.accept();
                    Connection clientConnection = onClientConnect(socket);
                    if (onClientConnected(clientConnection) && clientConnection != null) {
                        if (verbose) System.out.println("[Server] Client at " + clientConnection.getPort() + " successfully connected");
                        // TODO: Connection is successful

                    } else {
                        if (verbose) System.out.println("[Server] Client " + clientConnection.getPort() + " rejected");
                        // TODO: Client rejected
                    }
                }
            }

            @Override
            public boolean onComplete() {
                return true;
            }

            @Override
            public void onInitialise() {
                Server.this.serverThread = this.getTaskThread();
            }
        };
        serverContext.addTask(serverTask);
    }

    private Connection onClientConnect(Socket clientSocket) throws IOException {
        Connection<Server> connection = new Connection(serverContext, Connection.Owner.SERVER, clientSocket, this);
        return connection.connectToClient() ? connection : null;
    }

    public void stop() throws InterruptedException, IOException {
        isRunning = false;
        if (serverThread != null) {
            serverContext.stop();
            serverSocket.close();
            serverThread.join();
            if (verbose) System.out.println("[Server] Server down");
        }
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
