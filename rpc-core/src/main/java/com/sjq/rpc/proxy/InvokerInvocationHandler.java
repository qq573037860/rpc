package com.sjq.rpc.proxy;

import com.sjq.rpc.invoker.Invoker;
import com.sjq.rpc.domain.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InvokerInvocationHandler implements InvocationHandler {
    private final Invoker<?> invoker;

    public InvokerInvocationHandler(Invoker<?> handler) {
        this.invoker = handler;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获得方法名
        String methodName = method.getName();
        // 获得参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        // 如果类型是object，则直接反射调用
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        // 基础方法，不使用 RPC 调用
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        // rpc调用
        return invoker.invoke(new RpcRequest(method, args));
    }
}
