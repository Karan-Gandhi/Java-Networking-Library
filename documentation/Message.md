# Message

This class handles the connection between the server and client. It encloses the socket and sends the message to the server when I reads it from the socket. It is also responsible for writing the message to the socket. The server creates the connection object and calls the connectToClient method which will authenticate the client. The client will call the connectToServer method which will wait for the authentication message and then responds.

You can import this class by:
```java
import com.karangandhi.networking.utils.Message;
```

 * **Parameters:** `<T>` — The Class to which the connection belongs (TCPServer or TCPClient)

### `public enum Owner`

The enum that is passed in the constructor stating that the owner is the client or the server

### `public enum DefaultMessages`

The default messages of the connection that is used during the authentication

### `public interface onCloseExceptions`

This is a callback that is called if there is a exception that is thrown while closing the connection

### `public Connection(Context context, Owner owner, Socket socket, T ownerObject) throws IOException`

Creates an instance of the Connection class

 * **Parameters:**
   * `context` — Context of the Server or Client
   * `owner` — Owner.CLIENT or Owner.SERVER
   * `socket` — The socket between the client and the server
   * `ownerObject` — The TCPServer or TCPClient object
 * **Exceptions:** `IOException` — This is thrown if the socket is already closed

### `public boolean connectToServer()`

Connects the client to the server

This will initially wait and read the authentication token from the stream and then generates the corresponding encoded token and sends it back to the server.

Then it will wait for another message saying that the server is connected and will add the readMessage task to the context

 * **Returns:** True if the connection and the authentication is successful else returns false

### `public boolean connectToClient()`

Connects the server to the client

This will first generate a random token and then encode it twice. The first will be the send token and the second will be the expected token to be recieved. It sends the first token and then waits for a response. Once the client sends back a new token and it is same as the expected token then accepts it as a client

 * **Returns:** True if the authentication and connection is successful else return false

### `public void writeMessage()`

Writes the messages in the output message queue after setting the object is writing or not

### `public void addMessage(Message<?, ?> message)`

Adds the message to the output message queue and the calls the write message function if the connection is not writing to the output stream

 * **Parameters:** `message` — The message to be added

### `public void close(onCloseExceptions callback)`

This will close the connection

 * **Parameters:** `callback` — The callback that is a called when there is a error closing the connection

### `public UUID getId()`

Returns the connection id

 * **Returns:** The connection id

### `public Context getContext()`

Returns the context of the owner

 * **Returns:** The context

### `public int getPort()`

Returns the port of the connection

 * **Returns:** The port of the connection

### `public boolean isWriting()`

Returns if the connection is writing messages to the socket stream

 * **Returns:** True if the connection is writing messages to the socket stream

### `private Long encode(Long token)`

Encodes the token

 * **Parameters:** `token` — The Token to be encoded
 * **Returns:** A new encoded token
