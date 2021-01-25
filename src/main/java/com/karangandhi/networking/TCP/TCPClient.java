package com.karangandhi.networking.TCP;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.utils.Message;
import com.karangandhi.networking.core.TaskNotCompletedException;
import com.karangandhi.networking.utils.OwnerObject;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

/**
 * This is the TCPClient class which will create a client that connects to the server on the given port
 */
@SuppressWarnings({"unused", "rawtypes"})
public abstract class TCPClient implements OwnerObject {
    final private Connection<TCPClient> serverConnection;
    final private Context context;
    final private Socket clientSocket;

    final int serverPort;
    final int clientPort;

    final private boolean verbose;
    public boolean isRunning;

    /**
     * Creates an instance of TCPClient
     *
     * @param ip            The ip address of the server
     * @param port          The port of the server
     * @param verbose       If you want the client to be verbose
     * @throws IOException  Throws an IOException if no server exists on the given ip and port
     */
    public TCPClient(String ip, int port, boolean verbose) throws IOException {
        this.verbose = verbose;
        this.context = new Context();
        this.clientSocket = new Socket(ip, port);
        this.serverPort = port;
        this.serverConnection = new Connection<>(context, Connection.Owner.CLIENT, clientSocket, this);
        this.isRunning = false;
        clientPort = clientSocket.getPort();
    }

    /**
     * Abstract Method that is called when the client is connected to the server
     *
     * @return          True if you want the client to connect to the server
     */
    public abstract boolean onConnected();


    /**
     * This is a abstract method that is called when a message is recieved
     *
     * @param receivedMessage   Message that is recieved
     * @param connection        The connection of the server from which the message was recieved
     */
    public abstract void onMessageReceived(Message receivedMessage, Connection connection);

    /**
     * This is a abstract method which is called either when the client or the server closes the connection
     *
     * @param connection        The connection that is closed
     */
    public abstract void onDisConnected(Connection connection);

    /**
     * This is a method that calls onDisconnect when the server or the client closes the connection
     *
     * @param connection    Connection of the client that is closed
     */
    @Override
    public void clientConnectionClosed(Connection connection) {
        if (verbose) System.out.println("[Client] Disconnected from server");
        this.onDisConnected(connection);
    }

    /**
     * Detaches the connection. This doesn't do anything for the client
     *
     * @param connection    Connection to be detached
     */
    @Override
    public void detachConnection(Connection connection) {  }

    /**
     * This method sends a message to the server
     *
     * @param message       Message to be sent
     */
    public void sendMessage(Message message) {
        this.serverConnection.addMessage(message);
    }

    /**
     * This method starts the client and connects to the server
     *
     * @throws TaskNotCompletedException    An error is thrown when there is a error connecting to the server
     */
    public void start() throws TaskNotCompletedException {
        this.isRunning = true;
        boolean connectionStatus = this.serverConnection.connectToServer();
        if (connectionStatus && onConnected()) {
            if (verbose) System.out.println("[Client] Client connected to server");
        } else {
            serverConnection.close((Exception Ignored) -> { });
        }
        this.context.start();
    }

    /**
     * Stops the context and closes the connection
     */
    public void disconnect() {
        isRunning = false;
        serverConnection.close((Exception ignored) -> { });
        context.stop();
    }

    /**
     * To check if the client is verbose
     *
     * @return  True if the server is verbose;
     */
    @Override
    public boolean isVerbose() {
        return this.verbose;
    }

    /**
     * This method returns the connection of the client and the server
     *
     * @return  The connection between the client and the server
     */
    public Connection<TCPClient> getConnection() {
        return this.serverConnection;
    }

    /**
     * Gets the socket of the client
     *
     * @return  Socket of the client
     */
    public Socket getClientSocket() {
        return this.clientSocket;
    }

    /**
     * Get the port of the client
     *
     * @return  The port the client is connected to the server
     */
    public int getClientPort() {
        return clientPort;
    }

    /**
     * Get the port of the server
     *
     * @return  The port of the server the client is connected to
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Get the context of the client
     *
     * @return  The context of the client
     */
    public Context getContext() {
        return context;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TCPClient tcpClient = (TCPClient) o;
        return serverPort == tcpClient.serverPort &&
                clientPort == tcpClient.clientPort &&
                Objects.equals(serverConnection, tcpClient.serverConnection) &&
                Objects.equals(context, tcpClient.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverConnection, context, clientSocket, serverPort, clientPort, verbose, isRunning);
    }

    @Override
    public String toString() {
        return "TCPClient{" +
                "serverConnection=" + serverConnection +
                ", context=" + context +
                ", clientSocket=" + clientSocket +
                ", serverPort=" + serverPort +
                ", clientPort=" + clientPort +
                ", verbose=" + verbose +
                ", isRunning=" + isRunning +
                '}';
    }
}
