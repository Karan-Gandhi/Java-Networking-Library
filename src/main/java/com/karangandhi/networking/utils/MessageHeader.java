package com.karangandhi.networking.utils;

import java.io.*;
import java.util.Objects;

public class MessageHeader<T extends Enum<T>> implements Serializable {
    public T id;
    public long size;
    public static final int HEADER_SIZE = 190;

    public MessageHeader(T id) {
        this.id = id;
        this.size = 0;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public T getId() {
        return id;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.toByteArray(), 0, this.toByteArray().length);
    }

    public static MessageHeader readFrom(InputStream inputStream) throws IOException {
        byte[] array = new byte[MessageHeader.HEADER_SIZE];
        inputStream.read(array, 0, MessageHeader.HEADER_SIZE);
        return MessageHeader.fromByteArray(array);
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(this);
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

    public static MessageHeader fromByteArray(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = null;

        try {
            objectInput = new ObjectInputStream(byteArrayInputStream);
            //TODO: fix this
            MessageHeader header = (MessageHeader) objectInput.readObject();
            return header;
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
