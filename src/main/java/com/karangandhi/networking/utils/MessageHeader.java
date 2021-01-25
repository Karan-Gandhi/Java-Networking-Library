package com.karangandhi.networking.utils;

import java.io.*;
import java.util.Objects;

/**
 * Creates the MessageHeader that is read from the stream before the Message body
 *
 * @param <T>   The ID of the Message
 */
@SuppressWarnings("unused")
public class MessageHeader<T extends Enum<T>> implements Serializable {
    public T id;
    private long size;

    /**
     * Creates a instance of MessageHeader
     *
     * @param id        The ID of the Message
     */
    public MessageHeader(T id) {
        this.id = id;
        this.size = 0;
    }

    /**
     * Sets the Size of the message body
     *
     * @param size      The size to be set
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * Returns the size of the message body to be read
     *
     * @return      The size of the message body
     */
    public long getSize() {
        return size;
    }

    /**
     * Get the id of the message
     *
     * @return      The id of the message
     */
    public T getId() {
        return id;
    }

    /**
     * Write the message to the stream
     *
     * @param out           The stream to be written on
     * @throws IOException  This is thrown when there is a error while writing the message
     */
    public void writeTo(OutputStream out) throws IOException {
        new DataOutputStream(out).writeInt(this.toByteArray().length);
        out.write(this.toByteArray(), 0, this.toByteArray().length);
    }

    /**
     * Reads the message from the InputStream
     *
     * @param inputStream   The Stream for the message to be read from
     * @return              The built MessageHeader
     * @throws IOException  This is thrown when there is a error writing the message
     */
    public static MessageHeader<?> readFrom(InputStream inputStream) throws IOException {
        int headerSize = new DataInputStream(inputStream).readInt();
        byte[] array = new byte[headerSize];
        final int read = inputStream.read(array, 0, headerSize);
        return MessageHeader.fromByteArray(array);
    }

    /**
     * Converts the message to a byte array
     *
     * @return      THhe byte array
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
                if(objectOutputStream != null) objectOutputStream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Build the message from the Byte array
     *
     * @param bytes     The bytes from where the message should be read
     * @return          The Built message header
     */
    public static MessageHeader<?> fromByteArray(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = null;

        try {
            objectInput = new ObjectInputStream(byteArrayInputStream);
            return (MessageHeader<?>) objectInput.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        } finally {
            try {
                byteArrayInputStream.close();
                if (objectInput != null) objectInput.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "id=" + id +
                ", size=" + size +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageHeader<?> header = (MessageHeader<?>) o;
        return size == header.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, size);
    }
}