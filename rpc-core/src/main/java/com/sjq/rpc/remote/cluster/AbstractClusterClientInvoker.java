package com.sjq.rpc.remote.cluster;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.remote.DefaultFuture;
import com.sjq.rpc.remote.ExchangeClient;

import java.util.List;
import java.util.Objects;

public abstract class AbstractClusterClientInvoker implements ClusterClientInvoker {

    private Directory directory;

    AbstractClusterClientInvoker(Directory directory) {
        Objects.requireNonNull(directory);

        this.directory = directory;
    }

    @Override
    public DefaultFuture request(Request request, int timeout) {
        if (directory.isRegisterSupportBalance()) {//如果register支持负载均衡算法，则直接调用
            return directory.findWithRegister(request).request(request, timeout);
        }
        return doRequest(request, timeout);
    }

    protected List<ExchangeClient> getClients(Request request) {
        return directory.list(request);
    }

    abstract DefaultFuture doRequest(Request request, int timeout);
}
