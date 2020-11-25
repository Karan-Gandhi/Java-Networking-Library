package com.karangandhi.networking;

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

    public static void main(String[] args) throws TaskNotCompletedException, InterruptedException, IOException {
        Message<test, App> testAppMessage = new Message<test, App>(test.a, new App());
        FileOutputStream fileOutputStream = new FileOutputStream("testFiletxt.txt");
        testAppMessage.writeTo(fileOutputStream);
        fileOutputStream.close();
        FileInputStream fileInputStream = new FileInputStream("testFiletxt.txt");
        Message message = Message.readFrom(fileInputStream);
        System.out.println(testAppMessage.equals(message) + "\n" + testAppMessage + "\n" + message);
        fileInputStream.close();
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
