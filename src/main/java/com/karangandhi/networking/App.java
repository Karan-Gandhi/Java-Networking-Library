package com.karangandhi.networking;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Task;
import com.karangandhi.networking.core.TaskNotCompletedException;

public class App {
    public static void main(String[] args) throws TaskNotCompletedException, InterruptedException {
        Context context = new Context();
        context.start();
    }
}
