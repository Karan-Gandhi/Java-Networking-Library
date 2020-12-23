package com.karangandhi.networking.components;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Message;
import com.karangandhi.networking.core.TaskNotCompletedException;
import com.karangandhi.networking.utils.Constants;
import com.karangandhi.networking.utils.OwnerObject;
import com.karangandhi.networking.core.Task;
import com.karangandhi.networking.utils.Tasks;

import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.UUID;

import static com.karangandhi.networking.core.Debug.dbg;

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
                Message message = Message.readFrom(socketInputStream);
                long authenticationToken = encode((long) message.messageBody);

                Message newMessage = new Message(Connection.DefaultMessages.AUTHORISATION, authenticationToken);
                newMessage.writeTo(socketOutputStream);

                dbg("authenticationTokenRecieved = " + authenticationToken);

                this.tokenReceived = this.encode(authenticationToken);
                this.tokenSent = encode(tokenReceived);

                Message<DefaultMessages, Long> authenticationMessage = new Message<>(DefaultMessages.AUTHORISATION, tokenSent);
                authenticationMessage.writeTo(socketOutputStream);
                Message statusMessage = Message.readFrom(socketInputStream);

                if (statusMessage.getId() == DefaultMessages.CONNECTED) {
                    // TODO: Add the readMessage Task
                    dbg("Status Message = " + statusMessage);
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

                dbg("tokenSent = " + tokenSent + ", tokenExpected = " + tokenReceived);
                dbg("Header size = " + authenticationMessage.getHeaderSize());

                Message<DefaultMessages, Long> receivedTokenMessage = Message.readFrom(socketInputStream);

                if (receivedTokenMessage.getId() == DefaultMessages.AUTHORISATION && receivedTokenMessage.messageBody.equals(tokenReceived)) {
                    Message statusMessage = new Message<DefaultMessages, Serializable>(DefaultMessages.CONNECTED, null);
                    statusMessage.writeTo(socketOutputStream);

                    dbg("statusMessageHeaderSize = " + statusMessage.getHeaderSize());

                    Task readMessage = new Tasks.ServerTasks.ReadMessageTask(context, socketInputStream, (Message newMessage) -> {
                        ownerObject.onMessageReceived(newMessage, this);
                    });
                    context.addTask(readMessage);
                    if (!context.isRunning()) context.start();
                    return true;
                } else {
                    if (ownerObject.isVerbose()) System.out.println("[Connection] Closing Connection - Authentication failed: ID = " + receivedTokenMessage.getId() + ", Recieved Token = " + receivedTokenMessage.messageBody + ", Expected Token = " + this.tokenReceived + ", Authentication Status = " + (receivedTokenMessage.messageBody.equals(tokenReceived)));
                    ownerSocket.close();
                }
            } catch (IOException | TaskNotCompletedException exception) {
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
            while (!outMessageQueue.isEmpty()) {
                Message currentMessage = outMessageQueue.removeFirst();
                try {
                    currentMessage.writeTo(socketOutputStream);
                } catch (IOException exception) {
                    this.close();
                    ownerObject.detachConnection(this);
                }
            }
        }
    }

    public void readMessage() {
        synchronized (socketInputStream) {

        }
    }

    public void addMessage(Message message) {
        synchronized (outMessageQueue) {
            outMessageQueue.add(message);
            writeMessage();
        }
    }

    public void close() {
        try {
            this.socketInputStream.close();
            this.socketOutputStream.close();
            this.ownerSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
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
                ", id=" + id +
                ", socketInputStream=" + socketInputStream +
                ", socketOutputStream=" + socketOutputStream +
                ", tokenSent=" + tokenSent +
                ", tokenReceived=" + tokenReceived +
                '}';
    }
}
