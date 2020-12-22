package com.karangandhi.networking.core;

public class Debug {
    private static boolean debug = false;

    public static <T> void dbg(T message) {
        if(debug) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            StackTraceElement callerElement = stackTraceElements[3];
            System.out.println("[DEBUG] [" + callerElement.getClassName() + "." + callerElement.getMethodName() + ":" + callerElement.getLineNumber() + "] " + message);
        }
    }

    public static void setDebug(boolean status) {
        debug = status;
    }
}
