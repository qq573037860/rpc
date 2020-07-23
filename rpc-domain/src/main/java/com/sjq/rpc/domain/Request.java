package com.sjq.rpc.domain;

public interface Request {

    Long getId();

    String getInterfaceServiceFullName();

    String getMethodName();

    Class<?>[] getParameterTypes();

    Object[] getParameters();

    boolean isHeartbeat();

}
