package com.sjq.rpc.domain;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequest implements Request, Serializable {

    private static final AtomicLong INVOKE_ID = new AtomicLong(0);

    private Long id;

    private String interfaceServiceFullName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;

    private boolean isHeartHeat;

    public RpcRequest(boolean isHeartHeat) {
        this(true, null, null, null, null);
    }

    public RpcRequest(Method method, Object[] arguments) {
        this(false, method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes(), arguments);
    }

    public RpcRequest(boolean isHeartHeat, String interfaceServiceFullName, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        this.isHeartHeat = isHeartHeat;
        this.interfaceServiceFullName = interfaceServiceFullName;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.id = INVOKE_ID.incrementAndGet();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public String getInterfaceServiceFullName() {
        return interfaceServiceFullName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public boolean isHeartbeat() {
        return isHeartHeat;
    }
}
