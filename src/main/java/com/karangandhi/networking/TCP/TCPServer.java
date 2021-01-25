package com.karangandhi.networking.TCP;

import com.karangandhi.networking.core.*;
import com.karangandhi.networking.utils.Message;
import com.karangandhi.networking.utils.OwnerObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * This is the Server class and is used to create the tcp server.
 *
 * When the start method is called after creating a instance of a server the server
 * starts listening for clients at the given port. Once is client is connected the
 * server creates the Connection object which will authenticate the client. This
 * client connection is then passed to the onClientConnected method which should
 * return true if the client is accepted.
 *
 * The methods send and sendAll can be used to send messages to the client and the
 * onMessageReceived method is called when the server reads the message.
 *
 * You can remove a client by calling the removeClient method.
 *
 * Simply calling the stop server will stop the server (automatically close all
 * the connections)
 */
@SuppressWarnings({ "unused" })
public abstract class TCPServer implements OwnerObject {
    private final ArrayList<Connection<TCPServer>> clients;

    private final String ip;
    private final long port;
    private final int backlog;

    private final ServerSocket serverSocket;
    private final InetAddress ipInetAddress;
    private final Context serverContext;
    private Thread serverThread;

    public boolean isRunning;
    private final boolean verbose;

    /**
     * Creates an Instance of the server
     *
     * @param ip            The address of the server
     * @param port          The port at the address of the server
     * @param backlog       The backlog that the server can handle
     * @param verbose       This is true if you want the server to be verbose
     * @throws IOException  Throws an exception if the server exists on the given ip and port
     */
    public TCPServer(String ip, int port, int backlog, boolean verbose) throws IOException {
        this.ip = ip;
        this.port = port;
        this.isRunning = false;
        this.backlog = backlog;
        this.ipInetAddress = InetAddress.getByName(ip);
        this.serverSocket = new ServerSocket(port, backlog, this.ipInetAddress);
        this.serverContext = new Context();
        this.verbose = verbose;
        this.clients = new ArrayList<>();
    }

    /**
     * An abstract method that will be called when a client connects to the server
     *
     * @param clientConnection  An Connection object of the connection for the client
     * @return                  Must return true if you want to accept the client else return false
     */
    public abstract boolean onClientConnected(Connection<TCPServer> clientConnection);

    /**
     * An abstract method that will be called when the server receives a message from a client
     *  @param receivedMessage   The message recieved form the client
     * @param client            The connection object for the client
     */
    public abstract void onMessageReceived(Message<?, ?> receivedMessage, Connection<?> client);

    /**
     * An abstract method which will be called when a client gets disconnected
     *
     * @param clientConnection  The connection of the client which is disconnected
     */
    public abstract void onClientDisConnected(Connection<?> clientConnection);

    /**
     * This method disconnects the client that connected
     *
     * @param connection        The connection that is to be removed or detached
     */
    public void detachConnection(Connection<?> connection) {
        clients.remove(connection);
    }

    /**
     * This method will send the message to the specified client
     *
     * @param message           The message to be sent to the client
     * @param client            The connection of the client to send
     */
    public void sendMessage(Message<?, ?> message, Connection<TCPServer> client) {
        client.addMessage(message);
    }

    /**
     * This method will send the message to all the clients connected to the server
     *
     * @param message           The message to be sent to all the connected clients
     */
    public void sendAll(Message<?, ?> message) {
        for (Connection<TCPServer> client : this.clients) {
            client.addMessage(message);
        }
    }

    /**
     * This method will send a message to all the clients connected to the server except the client that will be excluded
     *
     * @param message           The message to be sent
     * @param excludeClient     The client to be excluded
     */
    public void sendAll(Message<?, ?> message, Connection<?> excludeClient) {
        for (Connection<?> client : this.clients) {
            if (client.equals(excludeClient)) continue;
            client.addMessage(message);
        }
    }

    /**
     * This method starts the server
     *
     * @throws TaskNotCompletedException    This exception is thrown if there is a error completing the task
     */
    public void start() throws TaskNotCompletedException {
        isRunning = true;
        waitForClientConnection();
        serverContext.start();
        if (verbose) System.out.println("[Server] Server started successfully");
    }

    /**
     * This method waits for a client to connect
     */
    private void waitForClientConnection() {
        for (int i = 0; i < ((this.backlog / 1000) == 0 ? 1 : this.backlog / 1000); i++) {
            Task serverTask = new Task(true, serverContext) {
                @Override
                public void run() throws IOException {
                    while (isRunning) {
                        Socket socket = serverSocket.accept();
                        Connection<TCPServer> clientConnection = onClientConnect(socket);
                        if (onClientConnected(clientConnection) && clientConnection != null) {
                            if (verbose)
                                System.out.println("[Server] Client at " + clientConnection.getPort() + " successfully connected");
                            clients.add(clientConnection);
                        } else {
                            if (verbose) System.out.println("[Server] Client rejected");
                        }
                    }
                }

                @Override
                public boolean onComplete(Exception exception) {
                    return exception != null;
                }

                @Override
                public void onInitialise() {
                    TCPServer.this.serverThread = this.getTaskThread();
                }
            };
            serverContext.addTask(serverTask);
        }
    }

    /**
     * This method is called when the client closes the connection
     *
     * @param connection    Closes the connection and notifies the server that the client has been disconnected
     */
    @Override
    public void clientConnectionClosed(Connection<?> connection) {
        if (verbose) System.out.println("[Server] Client at " + connection.getPort() + " disconnected");
        this.detachConnection(connection);
        connection.close((Exception e) -> { });
        this.onClientDisConnected(connection);
    }

    /**
     * @param clientSocket      Socket of the newly connected client
     * @return                  Returns a connection after the client is authenticated
     * @throws IOException      Throws an exception if the socket is already closed
     */
    private Connection<TCPServer> onClientConnect(Socket clientSocket) throws IOException {
        Connection<TCPServer> connection = new Connection<>(serverContext, Connection.Owner.SERVER, clientSocket, this);
        return connection.connectToClient() ? connection : null;
    }

    /**
     * Stops the Server
     *
     * @throws InterruptedException     Throws an interrupted exception if there is some error joining the thread
     * @throws IOException              Throws an IOException if there is a error closing the Server Socket
     */
    public void stop() throws InterruptedException, IOException {
        isRunning = false;
        if (serverThread != null) {
            serverContext.stop();
            serverSocket.close();
            serverThread.join();
            synchronized (this.clients) {
                for (Connection<TCPServer> client : this.clients) {
                    client.close((Exception ignored) -> { });
                }
            }
            this.clients.clear();
            if (verbose) System.out.println("[Server] Server stopped");
        }
    }

    /**
     * This method removes the client connected to the server
     *
     * @param client    Connection of the client to remove
     */
    public void removeClient(Connection<?> client) {
        clients.remove(client);
        client.close((Exception ignored) -> { });
    }

    /**
     * Get all the clients connected to the server
     *
     * @return  An arraylist of clients connected to the server
     */
    public ArrayList<Connection<TCPServer>> getClients() {
        return clients;
    }

    /**
     * @return  The port at which the server is connected
     */
    public long getPort() {
        return port;
    }

    /**
     * @return  The ip address of the server
     */
    public String getIp() {
        return ip;
    }

    /**
     * @return  The context of the server
     */
    public Context getServerContext() {
        return this.serverContext;
    }

    /**
     * @return  True if the server is verbose
     */
    public boolean isVerbose() {
        return this.verbose;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TCPServer server = (TCPServer) o;
        return port == server.port &&
                backlog == server.backlog &&
                Objects.equals(clients, server.clients) &&
                Objects.equals(ip, server.ip) &&
                Objects.equals(serverSocket, server.serverSocket) &&
                Objects.equals(ipInetAddress, server.ipInetAddress) &&
                Objects.equals(serverContext, server.serverContext) &&
                Objects.equals(serverThread, server.serverThread);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clients, ip, port, backlog, serverSocket, ipInetAddress, serverContext, serverThread, isRunning, verbose);
    }

    @Override
    public String toString() {
        return "Server{" +
                ", clients=" + clients +
                ", ip='" + ip + '\'' +
                ", port=" + port +
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
