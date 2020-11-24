package com.karangandhi.networking;

import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.core.MessageHeader;
import com.karangandhi.networking.core.TaskNotCompletedException;

import java.io.*;

public class App implements Serializable {
    public int a = 234;
    public boolean b = false;

    public static enum test {
        a, b, c, d, e, f, g, h, i, j
    }

    public static void main(String[] args) throws TaskNotCompletedException, InterruptedException, IOException {
        Message<test, App> testAppMessage = new Message<test, App>(test.a, new App());
        FileOutputStream fileOutputStream = new FileOutputStream("testFiletxt.txt");
        testAppMessage.writeTo(fileOutputStream);
        fileOutputStream.close();
        FileInputStream fileInputStream = new FileInputStream("testFiletxt.txt");
        Message message = Message.readFrom(fileInputStream);
        System.out.println(message);
        fileInputStream.close();

//        System.out.println();
    }

    @Override
    public String toString() {
        return "App{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
