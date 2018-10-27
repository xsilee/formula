package com.biddingo.framework.formula.utils;


public class FxRowItemIndexHolder {

    private static final ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>();

    public static void set(Integer rowIndex) {
        threadLocal.set(rowIndex);
    }

    public static Integer get() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }

}
