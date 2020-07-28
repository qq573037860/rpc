package com.sjq.rpc.invoker;

import com.sjq.rpc.domain.*;
import com.sjq.rpc.remote.*;
import com.sjq.rpc.remote.cluster.ClusterClientInvoker;

import java.util.Map;
import java.util.Objects;

public class DefaultInvoker<T> extends RpcInvoker<T> {

    private ClusterClientInvoker clusterClientInvoker;

    public DefaultInvoker(Class<T> interfaceType, ClusterClientInvoker clusterClientInvoker, int requestTimeout, Map<String, String> attachment) {
        super(interfaceType, requestTimeout, attachment);
        this.clusterClientInvoker = clusterClientInvoker;
    }

    @Override
    protected DefaultFuture doInvoke(Request request) {
        return clusterClientInvoker.request(request, getRequestTimeout());
    }

}
