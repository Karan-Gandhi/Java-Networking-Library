# MessageHeader

Creates the MessageHeader that is read from the stream before the Message body
You can import this class by:

```java
import com.karangandhi.networking.utils.MessageHeader;
```

 * **Parameters:** `<T>` — The ID of the Message

### `public MessageHeader(T id)`

Creates a instance of MessageHeader

 * **Parameters:** `id` — The ID of the Message

### `public void setSize(long size)`

Sets the Size of the message body

 * **Parameters:** `size` — The size to be set

### `public long getSize()`

Returns the size of the message body to be read

 * **Returns:** The size of the message body

### `public T getId()`

Get the id of the message

 * **Returns:** The id of the message

### `public void writeTo(OutputStream out) throws IOException`

Write the message to the stream

 * **Parameters:** `out` — The stream to be written on
 * **Exceptions:** `IOException` — This is thrown when there is a error while writing the message

### `public static MessageHeader<?> readFrom(InputStream inputStream) throws IOException`

Reads the message from the InputStream

 * **Parameters:** `inputStream` — The Stream for the message to be read from
 * **Returns:** The built MessageHeader
 * **Exceptions:** `IOException` — This is thrown when there is a error writing the message

### `public byte[] toByteArray()`

Converts the message to a byte array

 * **Returns:** THhe byte array

### `public static MessageHeader<?> fromByteArray(byte[] bytes)`

Build the message from the Byte array

 * **Parameters:** `bytes` — The bytes from where the message should be read
 * **Returns:** The Built message header
