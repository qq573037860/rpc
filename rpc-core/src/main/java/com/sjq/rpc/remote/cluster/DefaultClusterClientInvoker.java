package com.sjq.rpc.remote.cluster;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.remote.DefaultFuture;
import com.sjq.rpc.remote.ExchangeClient;
import io.netty.util.internal.ConcurrentSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DefaultClusterClientInvoker extends AbstractClusterClientInvoker {

    private final Set<String> calledKeySets = new ConcurrentSet<>();

    public DefaultClusterClientInvoker(Directory directory) {
        super(directory);
    }

    @Override
    DefaultFuture doRequest(Request request, int timeout) {
        List<ExchangeClient> clients = getClients(request);
        if (CollectionUtils.isEmpty(clients)) {
            throw new RpcException("no available service provider");
        }
        ExchangeClient client = null;
        for (ExchangeClient exchangeClient : clients) {
            if (!calledKeySets.contains(exchangeClient.getHostAddress())) {
                client = exchangeClient;
                break;
            }
        }
        if (Objects.isNull(client)) {
            client = clients.stream().findAny().get();
            calledKeySets.clear();
        }
        calledKeySets.add(client.getHostAddress());
        return client.request(request, timeout);
    }

}
