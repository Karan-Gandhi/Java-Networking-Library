package com.karangandhi.networking.utils;

import com.karangandhi.networking.TCP.Connection;

/**
 * This the class and the default methods that must be inherited by the server and
 * the client class
 */
public interface OwnerObject {
    /**
     * This will be called when the connection receives a message
     *
     * @param receivedMessage       The message that is recieved
     * @param client                The connection from where the message was recieved
     */
    void onMessageReceived(Message<?, ?> receivedMessage, Connection<?> client);

    /**
     * If the client or the server is verbose or not
     *
     * @return      If the client is verbose
     */
    boolean isVerbose();

    /**
     * Remove The message from the server (the client doesn't do much here)
     *
     * @param connection        The connection to be removed
     */
    void detachConnection(Connection<?> connection);

    /**
     * Called when the client closes the connection
     *
     * @param connection    The connection tha is closed
     */
    void clientConnectionClosed(Connection<?> connection);
}
