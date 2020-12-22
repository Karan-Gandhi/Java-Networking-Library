package com.karangandhi.networking;

import com.karangandhi.networking.components.Connection;
import com.karangandhi.networking.components.Server;
import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Debug;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.utils.OwnerObject;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import static com.karangandhi.networking.core.Debug.dbg;

public class App implements Serializable {
    public int a = 234;
    public boolean b = false;

    public static enum test {
        a, b, c, d, e, f, g, h, i, j
    }

    public static void main(String[] args) throws IOException {
        Debug.setDebug(true);

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
            server.start();

            Socket socket = new Socket("127.0.0.1", 8000);
            Connection client = new Connection(new Context(), Connection.Owner.CLIENT, socket, new OwnerObject() {

                @Override
                public void onMessageReceived(Message receivedMessage, Connection client) {
                    return;
                }

                @Override
                public boolean isVerbose() {
                    return false;
                }
            });

            client.connectToServer();
            Thread.sleep(3000);
//            for (Connection clients : server.getClients()) {
//                dbg(clients);
//            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("[Server] Server down");
        }
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

    public static Long encode(Long token) {
        // Hackers please don't read this
        Long newToken;
        if (token > 0) newToken = -token ^ 0xC0DEBEEF;
        else newToken = token ^ 0xC0DEBEEF;
        return newToken;
    }
}
