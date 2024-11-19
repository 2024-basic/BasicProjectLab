package com.even.labserver.utils;

public class LogUtils {
    private static String getCurrentStackTrace() {
        var currentMethod = Thread.currentThread().getStackTrace()[3];
        var tokens = currentMethod.getClassName().split("\\.");
        var className = tokens[tokens.length - 1];
        return className + "." + currentMethod.getMethodName() + "()";
    }

    public static String prefix() {
        return getCurrentStackTrace() + " - ";
    }
}
