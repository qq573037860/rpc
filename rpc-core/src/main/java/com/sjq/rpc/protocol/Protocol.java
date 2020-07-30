package com.sjq.rpc.protocol;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.invoker.Invoker;

public interface Protocol {

    <T>T referToProxy(Class<T> type, ServerConfig baseConfig) throws RpcException;

    <T> Invoker<T> referToInvoker(T proxy, Class<T> serviceType, ServerConfig baseConfig) throws RpcException;

    <T>T getProxy(Class<T> cls);
}
