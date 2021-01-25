package com.karangandhi.networking.utils;

import java.io.*;
import java.util.Objects;

/**
 * This is the message class that will be written and read from the stream of the socket.
 *
 * @param <T>       The Enum of which the id will be a instance of
 * @param <Q>       The datatype of the body
 */
@SuppressWarnings("unused")
public class Message <T extends Enum<T>, Q extends Serializable> implements Serializable {
    final private MessageHeader<T> messageHeader;
    public Q messageBody;

    /**
     * Creates a instance of a Message
     *
     * @param id            The id of the message
     * @param messageBody   The body of the message
     */
    public Message(T id, Q messageBody) {
        messageHeader = new MessageHeader<>(id);
        this.messageBody = messageBody;
        messageHeader.setSize(this.getBodySize());
    }

    /**
     * Creates a message with the given header and body
     *
     * @param header        The message header
     * @param messageBody   The message body
     */
    private Message(MessageHeader<T> header, Q messageBody) {
        this.messageHeader = header;
        this.messageBody = messageBody;
    }

    /**
     * @return      The Id of the message
     */
    public T getId() {
        return this.messageHeader.id;
    }

    /**
     * @return      An integer that is the size of the message header
     */
    public int getHeaderSize() {
        return this.messageHeader.toByteArray().length;
    }

    /**
     * Writes the message to the output stream
     *
     * @param outputStream      The output stream that you want to write the message to
     * @throws IOException      This is thrown when there is a error while writing to the stream
     */
    public void writeTo(OutputStream outputStream) throws IOException {
        messageHeader.writeTo(outputStream);
        outputStream.write(toByteArray());
    }

    /**
     * Reads a message form the input stream
     *
     * @param inputStream       The input stream that has to be read from
     * @return                  The Message that is built
     * @throws IOException      This is thrown when there is an error reading the message
     */
    public static Message<?, ?> readFrom(InputStream inputStream) throws IOException {
        MessageHeader<?> header = MessageHeader.readFrom(inputStream);
        int size = (int) header.getSize();
        byte[] array = new byte[size];
        final int read = inputStream.read(array);
        Object obj = fromByteArray(array);
        return new Message<>(header, (Serializable) obj);
    }

    /**
     * Converts the message object to a byte array
     *
     * @return      The converted byte array of the message
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this.messageBody);
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
     * Get the size of the body
     *
     * @return      An integer that is the size of the body size
     */
    public int getBodySize() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this.messageBody);
            objectOutputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes.length;
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
        return -1;
    }

    /**
     * Generates a message from a byte array
     *
     * @param bytes     The byte to be converted to the Message
     * @return          The generated message
     */
    public static Object fromByteArray(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = null;

        try {
            objectInput = new ObjectInputStream(byteArrayInputStream);
            return objectInput.readObject();
        } catch (Exception exception) {
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
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messageBody=" + messageBody +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message<?, ?> message = (Message<?, ?>) o;
        return Objects.equals(messageHeader, message.messageHeader) &&
                Objects.equals(messageBody, message.messageBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageHeader, messageBody);
    }
}
