package com.karangandhi.networking.components;

import com.karangandhi.networking.core.*;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.utils.OwnerObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;

import static com.karangandhi.networking.core.Debug.dbg;

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
        this.clients = new ArrayList<Connection>();
    }

    public abstract boolean onClientConnected(Connection clientConnection);

    public abstract void onMessageReceived(Message receivedMessage, Connection client);

    public abstract boolean onClientDisConnected(Connection clientConnection);

    public void detachConnection(Connection connection) {
        clients.remove(connection);
    }

    public void sendMessage(Message message, Connection client) {
        client.addMessage(message);
    }
    
    public void sendAll(Message message) {
        for (Connection client : this.clients) {
            client.addMessage(message);
        }
    }
    
    public void sendAll(Message message, Connection excludeClient) {
        for (Connection client : this.clients) {
            if (client.equals(excludeClient)) continue;
            client.addMessage(message);
        }
    }

    public void start() throws IOException, TaskNotCompletedException {
        // Running on another thread
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
                        clients.add(clientConnection);
                    } else {
                        if (verbose) System.out.println("[Server] Client rejected");
                        // TODO: Client rejected
                    }
                }
            }

            @Override
            public boolean onComplete(Exception exception) {
                return (exception == null) ? true : false;
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
            for (Connection client : this.clients) {
                synchronized (client) {
                    client.close((Exception ignored) -> {});
                }
            }
            this.clients.clear();
            if (verbose) System.out.println("[Server] Server stopped");
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

    public Context getServerContext() {
        return this.serverContext;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return port == server.port &&
                type == server.type &&
                backlog == server.backlog &&
                isRunning == server.isRunning &&
                verbose == server.verbose &&
                Objects.equals(readMessage, server.readMessage) &&
                Objects.equals(writeMessage, server.writeMessage) &&
                Objects.equals(clients, server.clients) &&
                Objects.equals(ip, server.ip) &&
                Objects.equals(serverSocket, server.serverSocket) &&
                Objects.equals(ipInetAddress, server.ipInetAddress) &&
                Objects.equals(serverContext, server.serverContext) &&
                Objects.equals(serverThread, server.serverThread);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readMessage, writeMessage, clients, ip, port, type, backlog, serverSocket, ipInetAddress, serverContext, serverThread, isRunning, verbose);
    }

    @Override
    public String toString() {
        return "Server{" +
                "readMessage=" + readMessage +
                ", writeMessage=" + writeMessage +
                ", clients=" + clients +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", type=" + type +
                ", backlog=" + backlog +
                ", serverSocket=" + serverSocket +
                ", ipInetAddress=" + ipInetAddress +
                ", serverContext=" + serverContext +
                ", serverThread=" + serverThread +
                ", isRunning=" + isRunning +
                ", verbose=" + verbose +
                '}';
    }
}
