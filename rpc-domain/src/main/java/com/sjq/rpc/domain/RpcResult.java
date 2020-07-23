package com.sjq.rpc.domain;

import java.io.Serializable;

public class RpcResult implements Result, Serializable {

    private static final long serialVersionUID = 8089338453495289729L;
    private Long id;
    private Object result;
    private Throwable exception;
    private boolean isHeartHeat;

    public RpcResult(Long id) {
        this(false, id, null, null);
    }

    public RpcResult(Long id, boolean isHeartHeat) {
        this(isHeartHeat, id, null, null);
    }

    public RpcResult(Long id, Object result, Throwable exception) {
        this(false, id, result, exception);
    }

    public RpcResult(boolean isHeartHeat, Long id, Object result, Throwable exception) {
        this.id = id;
        this.result = result;
        this.exception = exception;
        this.isHeartHeat = isHeartHeat;
    }

    @Override
    public Object getResult() throws Throwable {
        if (this.exception != null) {
            throw this.exception;
        } else {
            return this.result;
        }
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public void setException(Throwable e) {
        this.exception = e;
    }

    @Override
    public boolean isHeartbeat() {
        return isHeartHeat;
    }
}
