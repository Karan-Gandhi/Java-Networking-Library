package com.karangandhi.networking.components;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.core.Task;
import com.karangandhi.networking.core.TaskNotCompletedException;
import com.karangandhi.networking.utils.OwnerObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static com.karangandhi.networking.core.Debug.dbg;

public abstract class TCPClient implements OwnerObject {
    private Connection<TCPClient> serverConnection;
    private Context context;
    private Socket clientSocket;

    final int serverPort;
    final int clientPort;

    private boolean verbose;
    public boolean isRunning;

    public TCPClient(String ip, int port, boolean verbose) throws IOException {
        this.verbose = verbose;
        this.context = new Context();
        this.clientSocket = new Socket(ip, port);
        this.serverPort = port;
        this.serverConnection = new Connection<>(context, Connection.Owner.CLIENT, clientSocket, this);
        this.isRunning = false;
        clientPort = clientSocket.getPort();
    }

    public abstract boolean onConnected();

    public abstract void onMessageReceived(Message receivedMessage, Connection client);

    public abstract void onDisConnected(Connection clientConnection);

    public void clientConnectionClosed(Connection connection) {
        if (verbose) System.out.println("[Client] Disconnected from server");
        this.onDisConnected(connection);
    }

    @Override
    public void detachConnection(Connection connection) { }

    public void sendMessage(Message message) {
        this.serverConnection.addMessage(message);
    }

    public void start() throws TaskNotCompletedException {
        this.isRunning = true;
        boolean connectionStatus = this.serverConnection.connectToServer();
        if (connectionStatus && onConnected()) {
                // TODO: Connection is successful
            if (verbose) System.out.println("[Client] Client connected to server");
        } else {
            // TODO: Connection is unsuccessful
            serverConnection.close((Exception Ignored) -> { });
        }
        this.context.start();
    }

    public void disconnect() {
        serverConnection.close((Exception ignored) -> { });
    }

    @Override
    public boolean isVerbose() {
        return this.verbose;
    }

    public Connection getConnection() {
        return this.serverConnection;
    }
}
