package com.sjq.rpc.domain;

public interface Result {

    void setId(Long id);

    Long getId();

    Object getResult() throws Throwable;

    void setResult(Object result);

    Throwable getException();

    void setException(Throwable e);

    boolean isHeartbeat();

}
