package com.karangandhi.networking;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8000);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        System.out.println("[Client] Connected to server");
    }
}
