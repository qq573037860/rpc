package com.sjq.rpc.invoker;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.Result;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.remote.DefaultFuture;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class RpcInvoker<T> implements Invoker<T> {

    private Class<T> interfaceType;
    private Map<String, String> attachment;
    private int requestTimeout;

    public RpcInvoker(Class<T> interfaceType, int requestTimeout, Map<String, String> attachment) {
        this.interfaceType = interfaceType;
        this.requestTimeout = requestTimeout;
        this.attachment = attachment;
    }

    @Override
    public Object invoke(Request request) throws RpcException {
        try {
            DefaultFuture defaultFuture =  doInvoke(request);
            Result result = defaultFuture.get();
            return result.getResult();
        } catch (InterruptedException e) {
            throw new RpcException(RpcException.INTERRUPTED_EXCEPTION, e);
        } catch (ExecutionException e) {
            throw new RpcException(RpcException.EXECUTION_EXCEPTION, e);
        } catch (Throwable e) {
            throw new RpcException(RpcException.UNKNOWN_EXCEPTION, e);
        }
    }

    protected abstract DefaultFuture doInvoke(Request request);

    public Class<T> getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }
}
