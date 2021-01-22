package com.karangandhi.networking;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.ObjectLock;
import com.karangandhi.networking.utils.Message;
import com.karangandhi.networking.core.TaskNotCompletedException;
import com.karangandhi.networking.utils.OwnerObject;
import com.karangandhi.networking.core.Task;
import com.karangandhi.networking.utils.Tasks;

import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.UUID;

/**
 * This class handles the connection between the server and client. It encloses the socket and sends
 * the message to the server when I reads it from the socket. It is also responsible for writing the
 * message to the socket. The server creates the connection object and calls the connectToClient
 * method which will authenticate the client. The client will call the connectToServer method which
 * will wait for the authentication message and then responds.
 *
 * @param <T>   The Class to which the connection belongs (TCPServer or TCPClient)
 */
@SuppressWarnings({"unused", "unchecked"})
public class Connection<T extends OwnerObject> {
    /**
     * The enum that is passed in the constructor stating that the owner is the client or the server
     */
    public enum Owner { CLIENT, SERVER }

    /**
     * The default messages of the connection that is used during the authentication
     */
    public enum DefaultMessages { CONNECTED, AUTHORISATION }

    /**
     * This is a callback that is called if there is a exception that is thrown while closing the connection
     */
    public interface onCloseExceptions { void onCloseException(Exception e); }

    final private ArrayDeque<Message<?, ?>> outMessageQueue;

    final private Context context;
    final private Owner owner;
    final private Socket ownerSocket;
    final private T ownerObject;
    final private UUID id;

    final private InputStream socketInputStream;
    final private OutputStream socketOutputStream;

    private Long tokenSent, tokenReceived;

    final private ObjectLock isWriting;

    /**
     * Creates an instance of the Connection class
     *
     * @param context           Context of the Server or Client
     * @param owner             Owner.CLIENT or Owner.SERVER
     * @param socket            The socket between the client and the server
     * @param ownerObject       The TCPServer or TCPClient object
     * @throws IOException      This is thrown if the socket is already closed
     */
    public Connection(Context context, Owner owner, Socket socket, T ownerObject) throws IOException {
        this.context = context;
        this.outMessageQueue = new ArrayDeque<>();
        this.owner = owner;
        this.ownerSocket = socket;
        this.ownerObject = ownerObject;
        this.id = UUID.randomUUID();
        this.socketInputStream = socket.getInputStream();
        this.socketOutputStream = socket.getOutputStream();
        this.isWriting = new ObjectLock();
    }

    /**
     * Connects the client to the server
     *
     * This will initially wait and read the authentication token from the stream and then
     * generates the corresponding encoded token and sends it back to the server.
     *
     * Then it will wait for another message saying that the server is connected and will add
     * the readMessage task to the context
     *
     * @return      True if the connection and the authentication is successful else returns false
     */
    public boolean connectToServer() {
        if (owner == Owner.CLIENT) {
            try {
                Message<DefaultMessages, Long> message = Message.readFrom(socketInputStream);
                long authenticationToken = encode(message.messageBody);

                Message<DefaultMessages, Long> newMessage = new Message<>(Connection.DefaultMessages.AUTHORISATION, authenticationToken);
                newMessage.writeTo(socketOutputStream);

                this.tokenReceived = this.encode(authenticationToken);
                this.tokenSent = encode(tokenReceived);

                Message<DefaultMessages, Long> authenticationMessage = new Message<>(DefaultMessages.AUTHORISATION, tokenSent);
                authenticationMessage.writeTo(socketOutputStream);
                Message<DefaultMessages, ?> statusMessage = Message.readFrom(socketInputStream);

                if (statusMessage.getId() == DefaultMessages.CONNECTED) {
                    Task readMessage = new Tasks.ReadMessageTask(context,
                            socketInputStream,
                            (Message<?, ?> recievedMessage) -> ownerObject.onMessageReceived(recievedMessage, this),
                            () -> Connection.this.ownerObject.clientConnectionClosed(Connection.this));

                    context.addTask(readMessage);
                    if (!context.isRunning()) context.start();
                    return true;
                } else {
                    return false;
                }
            } catch (IOException | TaskNotCompletedException exception) {
                return false;
            }
        }
        return false;
    }

    /**
     * Connects the server to the client
     *
     * This will first generate a random token and then encode it twice. The first will be the send token
     * and the second will be the expected token to be recieved. It sends the first token and then waits
     * for a response. Once the client sends back a new token and it is same as the expected token then
     * accepts it as a client
     *
     * @return      True if the authentication and connection is successful else return false
     */
    public boolean connectToClient() {
        // TODO: add a inactivity timeout
        if (owner == Owner.SERVER) {
            try {
                long max = Long.MAX_VALUE;
                long min = 0;
                long randomToken = (long) (Math.random() * max);

                tokenSent = encode(randomToken);
                tokenReceived = encode(tokenSent);

                Message<DefaultMessages, Long> authenticationMessage = new Message<>(DefaultMessages.AUTHORISATION, tokenSent);
                authenticationMessage.writeTo(socketOutputStream);

                Message<DefaultMessages, Long> receivedTokenMessage = Message.readFrom(socketInputStream);

                if (receivedTokenMessage.getId() == DefaultMessages.AUTHORISATION && receivedTokenMessage.messageBody.equals(tokenReceived)) {
                    Message<DefaultMessages, Serializable> statusMessage = new Message<>(DefaultMessages.CONNECTED, null);
                    statusMessage.writeTo(socketOutputStream);

                    Task readMessage = new Tasks.ReadMessageTask(context,
                            socketInputStream,
                            (Message<?, ?> newMessage) -> ownerObject.onMessageReceived(newMessage, this),
                            () -> Connection.this.ownerObject.clientConnectionClosed(Connection.this));

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

    /**
     * Writes the messages in the output message queue after setting the object is writing or not
     */
    public void writeMessage() {
        synchronized (socketOutputStream) {
            this.isWriting.setLocked(true);
            while (!outMessageQueue.isEmpty()) {
                Message<?, ?> currentMessage = outMessageQueue.removeFirst();
                try {
                    currentMessage.writeTo(socketOutputStream);
                } catch (IOException exception) {
                    this.close((Exception ignored) -> { });
                    ownerObject.detachConnection(this);
                }
            }
            this.isWriting.setLocked(false);
        }
    }

    /**
     * Adds the message to the output message queue and the calls the write message function if the
     * connection is not writing to the output stream
     *
     * @param message       The message to be added
     */
    public void addMessage(Message<?, ?> message) {
        outMessageQueue.add(message);
        if (!this.isWriting()) this.writeMessage();
    }

    /**
     * This will close the connection
     *
     * @param callback      The callback that is a called when there is a error closing the connection
     */
    public void close(onCloseExceptions callback) {
        try {
            this.socketInputStream.close();
            this.socketOutputStream.close();
            this.ownerSocket.close();
        } catch (IOException exception) {
            callback.onCloseException(exception);
        }
    }

    /**
     * Returns the connection id
     *
     * @return      The connection id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the context of the owner
     *
     * @return      The context
     */
    public Context getContext() {
        return this.context;
    }

    /**
     * Returns the port of the connection
     *
     * @return      The port of the connection
     */
    public int getPort() {
        return ownerSocket.getPort();
    }

    /**
     * Returns if the connection is writing messages to the socket stream
     *
     * @return      True if the connection is writing messages to the socket stream
     */
    public boolean isWriting() {
        return this.isWriting.isLocked;
     }

    /**
     * Encodes the token
     *
     * @param token     The Token to be encoded
     * @return          A new encoded token
     */
    private Long encode(Long token) {
        // Hackers please don't read this
        return ~(token ^ 0xC0DEBEEF & 231973274);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection<?> that = (Connection<?>) o;
        return owner == that.owner &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outMessageQueue, context, owner, ownerSocket, ownerObject, id, socketInputStream, socketOutputStream, tokenSent, tokenReceived);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "outMessageQueue=" + outMessageQueue +
                ", ownerSocket=" + ownerSocket +
                ", id=" + id +
                ", socketInputStream=" + socketInputStream +
                ", socketOutputStream=" + socketOutputStream +
                ", tokenSent=" + tokenSent +
                ", tokenReceived=" + tokenReceived +
                '}';
    }
}
