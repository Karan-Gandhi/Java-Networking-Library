package com.karangandhi.networking;

import com.karangandhi.networking.components.Connection;
import com.karangandhi.networking.components.Server;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.core.MessageHeader;
import com.karangandhi.networking.core.TaskNotCompletedException;

import java.io.*;
import java.util.Objects;

public class App implements Serializable {
    public int a = 234;
    public boolean b = false;

    public static enum test {
        a, b, c, d, e, f, g, h, i, j
    }

    public static void main(String[] args) {
        Server server = null;
        try {
            server = new Server("127.0.0.1", 8000, Server.TCP, 100, true) {
                @Override
                public boolean onClientConnected(Connection clientConnection) {
                    return true;
                }

                @Override
                public void onMessageReceived(Message receivedMessage, Connection client) {
                    System.out.println("Recieved: " + receivedMessage + " from: " + client);
                }

                @Override
                public boolean onClientDisConnected(Connection clientConnection) {
                    return true;
                }
            };
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            server.start();
        } catch (IOException exception) {
            System.out.println("[Server] Server down");
        }
        try {
            server.stop();
        } catch (InterruptedException | IOException Ignored) {}
    }

    @Override
    public String toString() {
        return "App{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        App app = (App) o;
        return a == app.a &&
                b == app.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
