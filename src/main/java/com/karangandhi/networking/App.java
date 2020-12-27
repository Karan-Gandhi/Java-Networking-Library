package com.karangandhi.networking;

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
//        Debug.setDebug(true);

        Server server = null;
        try {
            server = new Server("127.0.0.1", 8000, Server.TCP, 0,100, true) {
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

            for (int x = 0; x < 10000; x++) {
                Server finalServer = server;
                new Thread(() -> {
                    Socket socket = null;
                    try {
                        socket = new Socket("127.0.0.1", 8000);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    Connection client = null;
                    try {
                        client = new Connection(new Context(), Connection.Owner.CLIENT, socket, new OwnerObject() {

                            @Override
                            public void onMessageReceived(Message receivedMessage, Connection client) {
                                dbg(receivedMessage);
                                return;
                            }

                            @Override
                            public boolean isVerbose() {
                                return false;
                            }

                            @Override
                            public void detachConnection(Connection connection) {
                                return;
                            }
                        });
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                    client.connectToServer();
                    // Need to add this as the client and the server are on the same file
                    finalServer.sendMessage(new Message(test.a, "Hello world"), finalServer.getClients().get(0));
                    Task task = null;
                    try {
                        Connection finalClient = client;
                        task = new Tasks.ServerTasks.ReadMessageTask(client.getContext(), socket.getInputStream(), (Message m) -> {
                            dbg(finalClient.getId() + ": " + m);
                        });
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    Task finalTask = task;
                    new Thread(() -> {
                        try {
                            finalTask.run();
                        } catch (IOException ignored) {
                        }
                    }).start();
    //                server.removeClient(server.getClients().get(0));
                    // sending 100 messages fast lets see how this holds up
                    Connection finalClient1 = client;
                    new Thread(() -> {
                        for (int i = 0; i < 1000; i++) finalClient1.addMessage(new Message(test.c, "ads"));
                        System.out.println("[DONE]");
                    }).start();
                }).start();
            }

//            {
//                Socket socket = new Socket("127.0.0.1", 8000);
//                Connection client = new Connection(new Context(), Connection.Owner.CLIENT, socket, new OwnerObject() {
//
//                    @Override
//                    public void onMessageReceived(Message receivedMessage, Connection client) {
//                        dbg(receivedMessage);
//                        return;
//                    }
//
//                    @Override
//                    public boolean isVerbose() {
//                        return false;
//                    }
//
//                    @Override
//                    public void detachConnection(Connection connection) {
//                        return;
//                    }
//                });
//
//                client.connectToServer();
//                // Need to add this as the client and the server are on the same file
//                server.sendMessage(new Message(test.a, "Hello world"), server.getClients().get(0));
//                Task task = new Tasks.ServerTasks.ReadMessageTask(client.getContext(), socket.getInputStream(), (Message m) -> {
//                    dbg(client.getId() + ": " + m);
//                });
//                new Thread(() -> {
//                    try {
//                        task.run();
//                    } catch (IOException ignored) {
//                    }
//                }).start();
//                new Thread(() -> {
//                    for (int i = 0; i < 10000; i++) client.addMessage(new Message(test.c, "ads"));
//                    System.out.println("[DONE]");
//                }).start();
////                server.removeClient(server.getClients().get(0));
//                client.addMessage(new Message(test.b, "Howdy"));
//            }

//            Server finalServer = server;
//            new Thread(() -> {
//                for (int i = 0; i < 10000; i++) finalServer.sendAll(new Message(test.b, "Howdy"));
//                System.out.println("[DONE]");
//            }).start();

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
