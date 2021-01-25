package com.karangandhi.networking.utils;

import com.karangandhi.networking.TCP.Connection;

public interface OwnerObject {
    public abstract void onMessageReceived(Message<?, ?> receivedMessage, Connection<?> client);
    public boolean isVerbose();
    public void detachConnection(Connection<?> connection);
    public void clientConnectionClosed(Connection<?> connection);
}
