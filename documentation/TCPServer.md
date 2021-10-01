# TCPServer

This is the Server class and is used to create the tcp server.

When the start method is called after creating a instance of a server the server starts listening for clients at the given port. Once is client is connected the server creates the Connection object which will authenticate the client. This client connection is then passed to the onClientConnected method which should return true if the client is accepted.

The methods send and sendAll can be used to send messages to the client and the onMessageReceived method is called when the server reads the message.

You can remove a client by calling the removeClient method.

Simply calling the stop server will stop the server (automatically close all the connections)

You can import this class by:
```java
import com.karangandhi.networking.TCP.TCPServer;
```

### `public TCPServer(String ip, int port, int backlog, boolean verbose) throws IOException`

Creates an Instance of the server

 * **Parameters:**
   * `ip` — The address of the server
   * `port` — The port at the address of the server
   * `backlog` — The backlog that the server can handle
   * `verbose` — This is true if you want the server to be verbose
 * **Exceptions:** `IOException` — Throws an exception if the server exists on the given ip and port

### `public abstract boolean onClientConnected(Connection<TCPServer> clientConnection)`

An abstract method that will be called when a client connects to the server

 * **Parameters:** `clientConnection` — An Connection object of the connection for the client
 * **Returns:** Must return true if you want to accept the client else return false

### `public abstract void onMessageReceived(Message<?, ?> receivedMessage, Connection<?> client)`

An abstract method that will be called when the server receives a message from a client

 * **Parameters:**
   * `receivedMessage` — The message recieved form the client
   * `client` — The connection object for the client

### `public abstract void onClientDisConnected(Connection<?> clientConnection)`

An abstract method which will be called when a client gets disconnected

 * **Parameters:** `clientConnection` — The connection of the client which is disconnected

### `public void detachConnection(Connection<?> connection)`

This method disconnects the client that connected

 * **Parameters:** `connection` — The connection that is to be removed or detached

### `public void sendMessage(Message<?, ?> message, Connection<TCPServer> client)`

This method will send the message to the specified client

 * **Parameters:**
   * `message` — The message to be sent to the client
   * `client` — The connection of the client to send

### `public void sendAll(Message<?, ?> message)`

This method will send the message to all the clients connected to the server

 * **Parameters:** `message` — The message to be sent to all the connected clients

### `public void sendAll(Message<?, ?> message, Connection<?> excludeClient)`

This method will send a message to all the clients connected to the server except the client that will be excluded

 * **Parameters:**
   * `message` — The message to be sent
   * `excludeClient` — The client to be excluded

### `public void start() throws TaskNotCompletedException`

This method starts the server

 * **Exceptions:** `TaskNotCompletedException` — This exception is thrown if there is a error completing the task

### `private void waitForClientConnection()`

This method waits for a client to connect

### `@Override public void clientConnectionClosed(Connection<?> connection)`

This method is called when the client closes the connection

 * **Parameters:** `connection` — Closes the connection and notifies the server that the client has been disconnected

### `private Connection<TCPServer> onClientConnect(Socket clientSocket) throws IOException`

 * **Parameters:** `clientSocket` — Socket of the newly connected client
 * **Returns:** Returns a connection after the client is authenticated
 * **Exceptions:** `IOException` — Throws an exception if the socket is already closed

### `public void stop() throws InterruptedException, IOException`

Stops the Server

 * **Exceptions:**
   * `InterruptedException` — Throws an interrupted exception if there is some error joining the thread
   * `IOException` — Throws an IOException if there is a error closing the Server Socket

### `public void removeClient(Connection<?> client)`

This method removes the client connected to the server

 * **Parameters:** `client` — Connection of the client to remove

### `public ArrayList<Connection<TCPServer>> getClients()`

Get all the clients connected to the server

 * **Returns:** An arraylist of clients connected to the server

### `public long getPort()`

 * **Returns:** The port at which the server is connected

### `public String getIp()`

 * **Returns:** The ip address of the server

### `public Context getServerContext()`

 * **Returns:** The context of the server

### `public boolean isVerbose()`

 * **Returns:** True if the server is verbose
