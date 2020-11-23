package com.karangandhi.networking;

import com.karangandhi.networking.core.Context;
import com.karangandhi.networking.core.Task;
import com.karangandhi.networking.core.TaskNotCompletedException;

public class App {
    public static void main(String[] args) throws TaskNotCompletedException {
        System.out.println("Hello, world");
        Context context = new Context();
        context.addTask(new Task.IDLE(context));
        context.start();
        context.addTask(new Task(true, context) {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                    System.out.println("Message from thread 1");
                }
            }

            @Override
            public boolean onComplete() {
                return true;
            }
        });
    }
}
