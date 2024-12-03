package com.even.labserver.utils;

public class LogUtils {
    /**
     * 현재 호출된 메소드의 이름을 반환한다.
     */
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
