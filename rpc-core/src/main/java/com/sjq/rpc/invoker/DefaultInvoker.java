package com.sjq.rpc.invoker;

import com.sjq.rpc.domain.*;
import com.sjq.rpc.remote.*;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultInvoker<T> extends RpcInvoker<T> {

    private ExchangeClientCluster client;

    public DefaultInvoker(Class<T> interfaceType, ExchangeClientCluster client, int requestTimeout, Map<String, String> attachment) {
        super(interfaceType, requestTimeout, attachment);
        this.client = client;
    }

    @Override
    protected DefaultFuture doInvoke(Request request) {
        AbstractExchangeClient exchangeClient = client.getClient();
        if (Objects.isNull(exchangeClient)) {
            throw new RpcException("no available service provider");
        }
        return exchangeClient.request(request, getRequestTimeout());
    }

}
