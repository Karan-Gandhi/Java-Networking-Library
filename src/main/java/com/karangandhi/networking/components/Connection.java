package com.karangandhi.networking.components;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.utils.OwnerObject;
import com.karangandhi.networking.core.Task;
import com.karangandhi.networking.utils.Tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.UUID;

public class Connection<T extends OwnerObject> {
    public enum Owner { CLIENT, SERVER }
    public enum DefaultMessages { CONNECTED, DISCONNECTED, PING, AUTHORISATION }

    private ArrayDeque<Message> outMessageQueue;

    private Context context;
    private Owner owner;
    private Socket ownerSocket;
    private T ownerObject;
    private UUID id;

    private InputStream socketInputStream;
    private OutputStream socketOutputStream;

    private Long tokenSent, tokenReceived;

    public Connection(Context context, Owner owner, Socket socket, T ownerObject) throws IOException {
        this.context = context;
        this.outMessageQueue = new ArrayDeque<Message>();
        this.owner = owner;
        this.ownerSocket = socket;
        this.ownerObject = ownerObject;
        this.id = UUID.randomUUID();
        this.socketInputStream = socket.getInputStream();
        this.socketOutputStream = socket.getOutputStream();
    }

    public boolean connectToServer() {
        if (owner == Owner.CLIENT) {
            try {
//                Long authenticationToken = (Long) Message.readFrom(socketInputStream).messageBody;
                InputStream inputStream = ownerSocket.getInputStream();
                OutputStream outputStream = ownerSocket.getOutputStream();
                Message message = Message.readFrom(inputStream);
                long authenticationToken = encode((long) message.messageBody);
                Message newMessage = new Message(Connection.DefaultMessages.AUTHORISATION, authenticationToken);
                newMessage.writeTo(outputStream);
                if (ownerObject.debug) System.out.println("[DEBUG] [Connection::connectToServer] authenticationTokenRecieved = " + authenticationToken);
                this.tokenReceived = this.encode(authenticationToken);
                this.tokenSent = encode(tokenReceived);
                Message<DefaultMessages, Long> authenticationMessage = new Message<>(DefaultMessages.AUTHORISATION, tokenSent);
                authenticationMessage.writeTo(socketOutputStream);
                Message statusMessage = Message.readFrom(socketInputStream);

                if (statusMessage.getId() == DefaultMessages.CONNECTED) {
                    // TODO: Add the readMessage Task

                    return true;
                } else {
                    return false;
                }
            } catch (IOException exception) {
                return false;
            }
        }
        return false;
    }

    public boolean connectToClient() {
        if (owner == Owner.SERVER) {
            try {
                long max = 0xFFFFFFFF;
                long min = 0;
                long randomToken = (long) (Math.random() * (max - min) + min);

                tokenSent = encode(randomToken);
                tokenReceived = encode(tokenSent);

                Message<DefaultMessages, Long> authenticationMessage = new Message<>(DefaultMessages.AUTHORISATION, tokenSent);
                authenticationMessage.writeTo(socketOutputStream);

                if (ownerObject.debug) System.out.println("[DEBUG] [Connection::connectToClient] tokenSent = " + tokenSent + ", tokenExpected = " + tokenReceived);
                if (ownerObject.debug) System.out.println("[DEBUG] [Connection::connectToClient] Header size = " + authenticationMessage.getHeaderSize());

                Message<DefaultMessages, Long> receivedTokenMessage = Message.readFrom(socketInputStream);

                if (receivedTokenMessage.getId() == DefaultMessages.AUTHORISATION && receivedTokenMessage.messageBody.equals(tokenReceived)) {
                    new Message<DefaultMessages, Serializable>(DefaultMessages.CONNECTED, null).writeTo(socketOutputStream);
                    Task readMessage = new Tasks.ServerTasks.ReadMessageTask(context, socketInputStream, (Message newMessage) -> {
                        ownerObject.onMessageReceived(newMessage, this);
                    });
                    context.addTask(readMessage);
                    return true;
                } else {
                    if (ownerObject.isVerbose()) System.out.println("[Connection] Closing Connection - Authentication failed: ID = " + receivedTokenMessage.getId() + ", Recieved Token = " + receivedTokenMessage.messageBody + ", Expected Token = " + this.tokenReceived + ", Authentication Status = " + (receivedTokenMessage.messageBody.equals(tokenReceived)));
                    ownerSocket.close();
                }
            } catch (IOException exception) {
                return false;
            }
        }
        return false;
    }

    public void disconnectFromServer() {
        if (owner == Owner.CLIENT) {

        }
    }
    
    public void disconnectFromClient() {
        if (owner == Owner.SERVER) {

        }
    }
    
    public void writeMessage() {
        synchronized (outMessageQueue) {

        }
    }

    public void readMessage() {
        synchronized (socketInputStream) {

        }
    }

    public UUID getId() {
        return id;
    }

    public int getPort() {
        return ownerSocket.getPort();
    }

    private Long encode(Long token) {
        // Hackers please don't read this
        Long newToken = token ^ 0xC0DEBEEF;
        return newToken;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "outMessageQueue=" + outMessageQueue +
                ", context=" + context +
                ", owner=" + owner +
                ", ownerSocket=" + ownerSocket +
                ", ownerObject=" + ownerObject +
                ", id=" + id +
                ", socketInputStream=" + socketInputStream +
                ", socketOutputStream=" + socketOutputStream +
                ", tokenSent=" + tokenSent +
                ", tokenReceived=" + tokenReceived +
                '}';
    }
}
