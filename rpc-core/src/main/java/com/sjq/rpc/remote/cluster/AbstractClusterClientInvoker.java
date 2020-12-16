package com.sjq.rpc.remote.cluster;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.remote.DefaultFuture;
import com.sjq.rpc.remote.ExchangeClient;

import java.util.List;
import java.util.Objects;

public abstract class AbstractClusterClientInvoker implements ClusterClientInvoker {

    private final Directory directory;

    AbstractClusterClientInvoker(Directory directory) {
        Objects.requireNonNull(directory);

        this.directory = directory;
    }

    @Override
    public DefaultFuture request(Request request, int timeout) {
        if (directory.useRegisterBalance()) {//是否使用register自带的负载均衡算法，则直接调用
            try {
                return directory.findWithRegister(request).request(request, timeout);
            } catch (RpcException e) {
            }
        }
        return doRequest(request, timeout);
    }

    List<ExchangeClient> getClients(Request request) {
        return directory.list(request);
    }

    abstract DefaultFuture doRequest(Request request, int timeout);
}
