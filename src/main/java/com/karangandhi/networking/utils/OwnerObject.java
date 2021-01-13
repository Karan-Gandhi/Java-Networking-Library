package com.karangandhi.networking.utils;

import com.karangandhi.networking.components.Connection;
import com.karangandhi.networking.core.Message;

import java.util.ArrayDeque;

public interface OwnerObject {
    public abstract void onMessageReceived(Message receivedMessage, Connection client);
    public boolean isVerbose();
    public void detachConnection(Connection connection);
}
