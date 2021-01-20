package com.karangandhi.networking.utils;

import java.io.*;
import java.util.Objects;

public class Message <T extends Enum<T>, Q extends Serializable> implements Serializable {
    private MessageHeader<T> messageHeader;
    public Q messageBody;

    public Message(T id, Q messageBody) {
        messageHeader = new MessageHeader<T>(id);
        this.messageBody = messageBody;
        messageHeader.setSize(this.getBodySize());
    }

    private Message(MessageHeader header, Q messageBody) {
        this.messageHeader = header;
        this.messageBody = messageBody;
    }

    public T getId() {
        return this.messageHeader.id;
    }

    public int getHeaderSize() {
        return this.messageHeader.toByteArray().length;
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        messageHeader.writeTo(outputStream);
        outputStream.write(toByteArray());
    }

    public static Message readFrom(InputStream inputStream) throws IOException {
        MessageHeader header = MessageHeader.readFrom(inputStream);
        int size = (int) header.getSize();
        byte[] array = new byte[size];
        inputStream.read(array);
        Object obj = fromByteArray(array);
        return new Message(header, (Serializable) obj);
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this.messageBody);
            objectOutputStream.flush();
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return bytes;
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

    public long getBodySize() {
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

    public static Object fromByteArray(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = null;

        try {
            objectInput = new ObjectInputStream(byteArrayInputStream);
            return objectInput.readObject();
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
