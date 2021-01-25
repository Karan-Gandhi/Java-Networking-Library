package com.karangandhi.networking.core;

/**
 * This is a class that helps improving the debugging, adds the class, method name and the line number
 * from where each method is called and where the debug was called.
 */
public class Debug {
    private static boolean debug = false;

    public static <T> void dbg(T message) {
        if(debug) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            try {
                StackTraceElement callerElement = stackTraceElements[2];
                System.out.println("[DEBUG] [" + callerElement.getClassName() + "." + callerElement.getMethodName() + ":" + callerElement.getLineNumber() + "] " + message);
            } catch (ArrayIndexOutOfBoundsException exception) {
                for (StackTraceElement element : stackTraceElements) {
                    System.out.print(element.getMethodName() + ", ");
                }
            }
        }
    }

    public static void setDebug(boolean status) {
        debug = status;
    }
}
