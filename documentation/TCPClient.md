# TCPClient

This is the TCPClient class which will create a client that connects to the server on the given port

### `public TCPClient(String ip, int port, boolean verbose) throws IOException`

Creates an instance of TCPClient

 * **Parameters:**
   * `ip` — The ip address of the server
   * `port` — The port of the server
   * `verbose` — If you want the client to be verbose
 * **Exceptions:** `IOException` — Throws an IOException if no server exists on the given ip and port

### `public abstract boolean onConnected()`

Abstract Method that is called when the client is connected to the server

 * **Returns:** True if you want the client to connect to the server

### `public abstract void onMessageReceived(Message receivedMessage, Connection connection)`

This is a abstract method that is called when a message is recieved

 * **Parameters:**
   * `receivedMessage` — Message that is recieved
   * `connection` — The connection of the server from which the message was recieved

### `public abstract void onDisConnected(Connection connection)`

This is a abstract method which is called either when the client or the server closes the connection

 * **Parameters:** `connection` — The connection that is closed

### `@Override public void clientConnectionClosed(Connection connection)`

This is a method that calls onDisconnect when the server or the client closes the connection

 * **Parameters:** `connection` — Connection of the client that is closed

### `@Override public void detachConnection(Connection connection)`

Detaches the connection. This doesn't do anything for the client

 * **Parameters:** `connection` — Connection to be detached

### `public void sendMessage(Message message)`

This method sends a message to the server

 * **Parameters:** `message` — Message to be sent

### `public void start() throws TaskNotCompletedException`

This method starts the client and connects to the server

 * **Exceptions:** `TaskNotCompletedException` — An error is thrown when there is a error connecting to the server

### `public void disconnect()`

Stops the context and closes the connection

### `@Override public boolean isVerbose()`

To check if the client is verbose

 * **Returns:** True if the server is verbose;

### `public Connection<TCPClient> getConnection()`

This method returns the connection of the client and the server

 * **Returns:** The connection between the client and the server

### `public Socket getClientSocket()`

Gets the socket of the client

 * **Returns:** Socket of the client

### `public int getClientPort()`

Get the port of the client

 * **Returns:** The port the client is connected to the server

### `public int getServerPort()`

Get the port of the server

 * **Returns:** The port of the server the client is connected to

### `public Context getContext()`

Get the context of the client

 * **Returns:** The context of the client
