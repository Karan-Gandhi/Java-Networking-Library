package com.karangandhi.networking;

import com.karangandhi.networking.components.Client;
import com.karangandhi.networking.components.Connection;
import com.karangandhi.networking.components.Server;
import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Debug;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.core.Task;
import com.karangandhi.networking.utils.OwnerObject;
import com.karangandhi.networking.utils.Tasks;
import org.omg.PortableServer.THREAD_POLICY_ID;

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
            server = new Server("127.0.0.1", 8000, 10000, true) {
                @Override
                public boolean onClientConnected(Connection clientConnection) {
                    return true;
                }

                @Override
                public void onMessageReceived(Message receivedMessage, Connection client) {
                    dbg("Recieved: " + receivedMessage + " from: " + client);
                }

                @Override
                public void onClientDisConnected(Connection clientConnection) {
                    return;
                }
            };
            server.start();

            Client client = new Client("127.0.0.1", 8000, true) {

                @Override
                public boolean onConnected() {
                    return true;
                }

                @Override
                public void onMessageReceived(Message receivedMessage, Connection client) {
                    dbg("Recieved: " + receivedMessage + " from: " + client);
                }

                @Override
                public void onDisConnected(Connection clientConnection) {

                }
            };
            client.start();
            Thread.sleep(1000);
            client.sendMessage(new Message(test.b, "Hello, world"));
            server.sendAll(new Message(test.b, "Howdy"));
//            server.stop();
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
