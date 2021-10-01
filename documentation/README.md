# Documentation
**Note: If you want the complete documentation of this project please visit [this link](https://karan-gandhi.github.io/Java-Networking-Library/)**
## Index
- [TCPServer](https://github.com/Karan-Gandhi/Java-Networking-Library/blob/master/documentation/TCPServer.md)
  - This is the Server class and is used to create the tcp server.
When the start method is called after creating a instance of a server the server starts listening for clients at the given port. Once is client is connected the server creates the Connection object which will authenticate the client. This client connection is then passed to the onClientConnected method which should return true if the client is accepted.
- [TCPClient](https://github.com/Karan-Gandhi/Java-Networking-Library/blob/master/documentation/TCPClient.md)
  - This is the TCPClient class which will create a client that connects to the server on the given port
- [Message](https://github.com/Karan-Gandhi/Java-Networking-Library/blob/master/documentation/Message.md)
  - This class handles the connection between the server and client. It encloses the socket and sends the message to the server when I reads it from the socket. It is also responsible for writing the message to the socket. The server creates the connection object and calls the connectToClient method which will authenticate the client. The client will call the connectToServer method which will wait for the authentication message and then responds.
- [MessageHeadder](https://github.com/Karan-Gandhi/Java-Networking-Library/blob/master/documentation/MessageHeader.md)
  - Creates the MessageHeader that is read from the stream before the Message body
- [Connection](https://github.com/Karan-Gandhi/Java-Networking-Library/blob/master/documentation/Connection.md)
  - This class handles the connection between the server and client. It encloses the socket and sends the message to the server when I reads it from the socket. It is also responsible for writing the message to the socket. The server creates the connection object and calls the connectToClient method which will authenticate the client. The client will call the connectToServer method which will wait for the authentication message and then responds.
