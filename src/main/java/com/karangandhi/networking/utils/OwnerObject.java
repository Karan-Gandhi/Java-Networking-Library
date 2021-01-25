package com.karangandhi.networking.utils;

import com.karangandhi.networking.TCP.Connection;

public interface OwnerObject {
    void onMessageReceived(Message<?, ?> receivedMessage, Connection<?> client);
    boolean isVerbose();
    void detachConnection(Connection<?> connection);
    void clientConnectionClosed(Connection<?> connection);
}
