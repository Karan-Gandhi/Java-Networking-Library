# Java-Networking-Library

[![](https://jitpack.io/v/Karan-Gandhi/Java-Networking-Library.svg)](https://jitpack.io/#Karan-Gandhi/Java-Networking-Library)

## Overview

This is a networking library written in java which helps creating tcp servers faster. Now it is easy to create TCP servers just with a few lines of code without much experience in networking. This library gives flexiblity and allows you to send custom objects through the socket stream. The documentation is available [here](https://karan-gandhi.github.io/Java-Networking-Library/)

This library runs on **Android** as well as on **Desktop**

- [Quickstart](#quickstart)
- [Dependency](#dependency)
  - [Gradle](#gradle)
  - [Maven](#maven)
- [Licence](#license)
- [QuickLinks](#quicklinks)

## QuickStart

Here's a sample code that simply creates a client and a server and sends a message. For more details you can visit the [Documentation page](https://karan-gandhi.github.io/Java-Networking-Library/)

The Server class
```Java
public class App {
    public static enum Methods { GREET, JOIN }
    
    public static void main(String[] args) {
        try {
            // Creating a server on IP address 127.0.0.1 (localhost) at port 80
            TCPServer server = new TCPServer(/*IP: */ 127.0.0.1, /*Port: */ 80, /*Backlog: */ 100, /*Verbose: */ true) {
                @Override
                public boolean onClientConnected(Connection clientConnection) {
                    // Here if we return false the client will be rejected. 
                    // For this example we will except all clients who connect 
                    // to the server and are authenticated by the server
                    System.out.println("Client Connected");
                    return true;
                }

                @Override
                public void onMessageReceived(Message receivedMessage, Connection client) {
                    if (recievedMessage.getId() == Methods.GREET) {
                        // Print the message recieved
                        System.out.println("Message recieved: " + receivedMessage.messageBody);
                        
                        // Build the message to send
                        Message<Methods, String> messageToSend = new Message<>(Methods.GREET, "Hello");
                        
                        // Send the message to the client
                        this.sendMessage(messageToSend, client);
                        
                        // Build the message to send it to everyone
                        Message<Methods, String> messageToBrodcast = new Message<>(Methods.JOIN, client.getPort());
                        // Send the message to Everyone
                        this.sendAll(messageToBrodcast);
                    }
                }

                @Override
                public void onClientDisConnected(Connection clientConnection) {
                    // This will be called when the client disconnects
                    System.out.println("A client was disconnected");
                }
            }
            
            // This will start the server
            server.start();
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("[Server] Server down");
        }
    }
}
```

And the corresponding client class

```Java
public class App {
    public static enum Methods { GREET, JOIN }
    
    public static void main(String[] args) {
        try {
            TCPClient client = new TCPClient(/*IP: */ "127.0.0.1", /*Port: */ 80, /*Verbose: */ true) {
                @Override
                public boolean onConnected() {
                    System.out.println("Connected to the Server");
                    return true;
                }

                @Override
                public void onMessageReceived(Message receivedMessage, Connection client) {
                    if (recievedMessage.getId() == Methods.GREET) {
                        // Print the message recieved
                        System.out.println("Message recieved: " + receivedMessage.messageBody);
                    } else if (recievedMessage.getId() == Methods.JOIN) {
                        // Print the details of the client connected
                        System.out.println("A New client joined the server at port: " + recievedMessage.messageBody);
                    }
                }

                @Override
                public void onDisConnected(Connection clientConnection) {
                    System.out.println("Disconnected from the server");
                }
            };
            // Start the client ie connect it to the server
            client.start();
            
            // Send a message from the Client to the server
            client.sendMessage(new Message(Methods.GREET, "Hello from client"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
```
To stop the server and the client you can call the `stop()` method.

## Dependency

### Gradle

Add JitPack repository to root `build.gradle` for android
```Gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
For Gradle:

```Gradle
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}
```

And finally add the dependency
```Gradle
dependencies {
    implementation 'com.github.Karan-Gandhi:Java-Networking-Library:v1.0'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.Karan-Gandhi</groupId>
    <artifactId>Java-Networking-Library</artifactId>
    <version>v1.0</version>
</dependency>
```

## License

```
MIT License

Copyright (c) 2020 Karan Gandhi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## QuickLinks
- [Documentation](https://karan-gandhi.github.io/Java-Networking-Library/)
