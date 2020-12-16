package com.sjq.rpc.remote.cluster;

import com.google.common.collect.Lists;
import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.remote.ChannelHandler;
import com.sjq.rpc.remote.DefaultExchangeClient;
import com.sjq.rpc.remote.ExchangeClient;
import com.sjq.rpc.remote.Transporters;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StaticDirectory implements Directory {

    private final ServerConfig serviceConfig;
    private final ChannelHandler handler;

    private final Map<String, ExchangeClient> CLIENT_MAP = new ConcurrentHashMap<>();

    public StaticDirectory(ServerConfig serviceConfig, ChannelHandler handler) {
        this.serviceConfig = serviceConfig;
        this.handler = handler;

        init();
    }

    public void init() {
        for (URI uri : serviceConfig.getServerUrls()) {
            String key = getKey(uri.getHost(), uri.getPort());
            CLIENT_MAP.computeIfAbsent(key, value ->
                    new DefaultExchangeClient(Transporters.getTransporter().connect(uri.getHost(), uri.getPort(), serviceConfig, handler)));
        }
    }

    @Override
    public List<ExchangeClient> list(Request request) {
        return Lists.newArrayList(CLIENT_MAP.values());
    }

}
