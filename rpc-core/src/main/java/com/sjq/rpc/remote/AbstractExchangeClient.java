package com.sjq.rpc.remote;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;

public abstract class AbstractExchangeClient implements Client {

    public DefaultFuture request(Request request, int timeout) {
        return doRequest(request, timeout);
    }

    protected abstract DefaultFuture doRequest(Request request, int timeout) throws RpcException;
}
