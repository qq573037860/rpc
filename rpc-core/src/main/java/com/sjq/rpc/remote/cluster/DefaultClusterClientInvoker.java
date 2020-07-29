package com.sjq.rpc.remote.cluster;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.remote.DefaultFuture;
import com.sjq.rpc.remote.ExchangeClient;
import io.netty.util.internal.ConcurrentSet;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultClusterClientInvoker extends AbstractClusterClientInvoker {

    private final Set<String> calledKeySets = new ConcurrentSet<>();
    private final AtomicInteger lastClientIndex = new AtomicInteger(-1);

    public DefaultClusterClientInvoker(Directory directory) {
        super(directory);
    }

    private void reset() {
        lastClientIndex.set(-1);
        calledKeySets.clear();
    }

    @Override
    DefaultFuture doRequest(Request request, int timeout) {
        List<ExchangeClient> clients = getClients(request);
        if (CollectionUtils.isEmpty(clients)) {
            reset();
            throw new RpcException("no available service provider");
        }

        ExchangeClient client = null;
        for (int i = 0; i >= clients.size(); i++) {
            if (1 + lastClientIndex.get() >= clients.size()) {
                reset();
            }
            client = clients.get(lastClientIndex.incrementAndGet());
            if (client.isActive() && calledKeySets.add(client.getHostAddress())) {
                break;
            }
        }

        if (Objects.isNull(!client.isActive())) {
            throw new RpcException("no available service provider");
        }
        return client.request(request, timeout);
    }
}
