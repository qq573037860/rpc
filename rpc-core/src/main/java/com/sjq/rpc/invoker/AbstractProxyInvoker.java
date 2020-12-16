package com.sjq.rpc.invoker;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InvokerWrapper
 */
public abstract class AbstractProxyInvoker<T> implements Invoker<T> {

    Logger logger = LoggerFactory.getLogger(AbstractProxyInvoker.class);

    private final T proxy;

    public AbstractProxyInvoker(T proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy == null");
        }
        this.proxy = proxy;
    }

    @Override
    public Class<T> getInterfaceType() {
        return (Class<T>) proxy.getClass();
    }

    @Override
    public Object invoke(Request request) throws RpcException {
        try {
            return doInvoke(proxy, request.getMethodName(), request.getParameterTypes(), request.getParameters());
        } catch (Throwable e) {
            throw new RpcException("Failed to invoke proxy method " + request.getMethodName() + ", cause: " + e.getMessage(), e);
        }
    }

    protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable;

}
