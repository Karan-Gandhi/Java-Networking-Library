package com.karangandhi.networking.utils;

import com.karangandhi.networking.components.Connection;
import com.karangandhi.networking.core.Message;

import java.util.ArrayDeque;

public interface OwnerObject {
    ArrayDeque<Message> readMessage = new ArrayDeque<>();
    ArrayDeque<Message> writeMessage = new ArrayDeque<>();

    public abstract void onMessageReceived(Message receivedMessage, Connection client);
    public abstract boolean isVerbose();
}
