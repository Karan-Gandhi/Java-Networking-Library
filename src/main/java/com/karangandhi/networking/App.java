package com.karangandhi.networking;

import com.karangandhi.networking.components.Connection;
import com.karangandhi.networking.components.Server;
import com.karangandhi.networking.utils.Message;
import com.karangandhi.networking.utils.MessageHeader;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class App implements Serializable {
    public int a = 234;
    public boolean b = false;

    public static enum test {
        a, b, c, d, e, f, g, h, i, j
    }

    public static void main(String[] args) throws IOException {
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
            InputStream stream = socket.getInputStream();
            OutputStream stream1 = socket.getOutputStream();
            Thread.sleep(7000);
            Message recievedMessage = Message.readFrom(stream);
            System.out.println(recievedMessage);
//            server.stop();
        } catch (Exception exception) {
            System.out.println("[Server] Server down");
            exception.printStackTrace();
        }

        FileOutputStream fileOutputStream = new FileOutputStream("testFiletxt.bin");

        Message me = new Message(test.a, 1);
        me.writeTo(fileOutputStream);

        FileInputStream fileInputStream = new FileInputStream("testFiletxt.bin");
        Message ne = Message.readFrom(fileInputStream);

        System.out.println();

//        Message me = new Message(test.a, 1);
//        me.writeTo(fileOutputStream);

//        InputStream stream = new
//
//
//        Message header = new Message(test.a, 9);
//        byte[] bytes = header.toByteArray();
//        header.writeTo(fileOutputStream);
//        System.out.println("[Client] : ");
//        for(byte b : bytes) {
//            System.out.print(b + " ");
//        }
//        fileOutputStream.close();
//        FileInputStream fileInputStream = new FileInputStream("testFiletxt.bin");
//        MessageHeader newHeader = MessageHeader.readFrom(fileInputStream);
//        fileInputStream.close();

//        System.out.print(newHeader);

//        Message<test, App> testAppMessage = new Message<test, App>(test.a, new App());
//        testAppMessage.writeTo(fileOutputStream);
////        Message message = Message.readFrom(fileInputStream);
////        System.out.println(testAppMessage.equals(message) + "\n" + testAppMessage + "\n" + message);
//        byte[] expectedArray = testAppMessage.messageHeader.toByteArray();
//        for (byte b : expectedArray) {
//            System.out.print(b + " ");
//        }
//
//        System.out.println();
//        System.out.println(expectedArray.length);
//
//        byte[] gotArray = new byte[MessageHeader.HEADER_SIZE];
//        fileInputStream.read(gotArray);
//
//        for (byte b : gotArray) {
//            System.out.print(b + " ");
//        }
//
//        System.out.println();
//        System.out.println();
//
//        for (int i = 0; i < MessageHeader.HEADER_SIZE; i++) {
//            System.out.print((expectedArray[i] == gotArray[i] ? 1 : 0) + " ");
//        }
//
//        System.out.println();
//
//        MessageHeader header = MessageHeader.fromByteArray(gotArray);
//        System.out.println(header);
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
        Long newToken = token ^ 0xC0DEBEEF;
        newToken >>= 12345;
        newToken &= 0x12C0DE34;
        return newToken;
    }
}
