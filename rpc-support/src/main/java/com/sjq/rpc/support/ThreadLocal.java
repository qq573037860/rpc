package com.sjq.rpc.support;

import java.util.Objects;

public class ThreadLocal<T> {

    private static final java.lang.ThreadLocal LOCAL = new java.lang.ThreadLocal<>();

    public T get() {
        Thread thread = Thread.currentThread();
        Object data = null;
        if (thread instanceof InternalThread) {
            data = ((InternalThread)thread).getData();
        } else {
            data = (T) LOCAL.get();
        }

        if (Objects.isNull(data)) {
            try {
                data = initialValue();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return (T) data;
    }

    public void set(T data) {
        Thread thread = Thread.currentThread();
        if (thread instanceof InternalThread) {
            ((InternalThread)thread).setData(data);
        } else {
            LOCAL.set(data);
        }
    }

    public void remove() {
        Thread thread = Thread.currentThread();
        if (thread instanceof InternalThread) {
            ((InternalThread)thread).remove();
        } else {
            LOCAL.remove();
        }
    }

    protected T initialValue() throws Exception {
        return null;
    }
}
