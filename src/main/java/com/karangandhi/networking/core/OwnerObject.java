package com.karangandhi.networking.core;

import java.util.ArrayDeque;

public interface OwnerObject {
    ArrayDeque<Message> readMessage = new ArrayDeque<>();
    ArrayDeque<Message> writeMessage = new ArrayDeque<>();
}
