package com.sjq.rpc.domain;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class RpcException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -2322011732212152261L;
    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;
    public static final int BIZ_EXCEPTION = 3;
    public static final int FORBIDDEN_EXCEPTION = 4;
    public static final int SERIALIZATION_EXCEPTION = 5;
    public static final int INVALID_ARGUMENT_EXCEPTION = 6;
    public static final int INTERRUPTED_EXCEPTION = 7;
    public static final int EXECUTION_EXCEPTION = 8;

    private int code;

    public RpcException() {
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(int code) {
        this.code = code;
    }

    public RpcException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public RpcException(int code, String message) {
        super(message);
        this.code = code;
    }

    public RpcException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public boolean isBiz() {
        return this.code == 3;
    }

    public boolean isForbidded() {
        return this.code == 4;
    }

    public boolean isTimeout() {
        return this.code == 2;
    }

    public boolean isNetwork() {
        return this.code == 1;
    }

    public boolean isSerialization() {
        return this.code == 5;
    }
}
