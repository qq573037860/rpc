package com.sjq.rpc.remote;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.remote.cluster.Cluster;
import com.sjq.rpc.remote.cluster.ClusterClientInvoker;
import com.sjq.rpc.remote.cluster.RegisterDirectory;
import com.sjq.rpc.remote.cluster.StaticDirectory;
import com.sjq.rpc.remote.transport.DefaultChannelHandlerDelegate;
import com.sjq.rpc.remote.transport.HeartbeatHandler;
import com.sjq.rpc.support.spi.ServiceLoader;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

public class Transporters {

    private Transporters() {}

    private static ChannelHandler handlerWrapper() {
        return new HeartbeatHandler(new RequestDispatchHandler(new DefaultChannelHandlerDelegate()));
    }

    private static ChannelHandler handlerWrapper(ExchangeHandler...handler) {
        return new HeartbeatHandler(new RequestDispatchHandler(Arrays.asList(handler).stream().
                map(h -> new DefaultChannelHandlerDelegate(h)).collect(Collectors.toList())));
    }

    public static Server bind(ServerConfig serverConfig, ExchangeHandler...handler) {
        return getTransporter().bind(serverConfig, handlerWrapper(handler));
    }

    public static ClusterClientInvoker connect(ServerConfig serverConfig) {
        return getCluster().join(serverConfig.isRegisterCenterForClient()
            ? new RegisterDirectory(serverConfig, handlerWrapper())
            : new StaticDirectory(serverConfig, handlerWrapper()));
    }

    public static Transporter getTransporter() {
        return ServiceLoader.load(Transporter.class);
    }

    public static Cluster getCluster() {
        return ServiceLoader.load(Cluster.class);
    }
}
