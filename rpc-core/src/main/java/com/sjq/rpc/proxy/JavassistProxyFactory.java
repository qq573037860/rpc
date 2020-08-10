package com.sjq.rpc.proxy;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.invoker.AbstractProxyInvoker;
import com.sjq.rpc.invoker.Invoker;
import com.sjq.rpc.support.proxy.Proxy;
import com.sjq.rpc.support.proxy.Wrapper;

public class JavassistProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Invoker<T> invoker) throws RpcException {
        return (T) Proxy.getProxy(invoker.getInterfaceType()).newInstance(new InvokerInvocationHandler(invoker));
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy) throws RpcException {
        final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass());
        return new AbstractProxyInvoker<T>(proxy) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
    }
}
