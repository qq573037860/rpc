package com.sjq.rpc.proxy;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.invoker.Invoker;

public interface ProxyFactory {

    <T> T getProxy(Invoker<T> invoker) throws RpcException;

    <T> Invoker<T> getInvoker(T proxy) throws RpcException;

}
