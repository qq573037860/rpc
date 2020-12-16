package com.sjq.rpc.remote;

import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.remote.cluster.Cluster;
import com.sjq.rpc.remote.cluster.ClusterClientInvoker;
import com.sjq.rpc.remote.cluster.RegisterDirectory;
import com.sjq.rpc.remote.cluster.StaticDirectory;
import com.sjq.rpc.remote.transport.DefaultChannelHandlerDelegate;
import com.sjq.rpc.remote.transport.HeartbeatHandler;
import com.sjq.rpc.support.spi.ServiceLoaders;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Transporters {

    private Transporters() {}

    private static ChannelHandler handlerWrapper() {
        return new HeartbeatHandler(new RequestDispatchHandler(new DefaultChannelHandlerDelegate()));
    }

    private static ChannelHandler handlerWrapper(ExchangeHandler...handler) {
        return new HeartbeatHandler(new RequestDispatchHandler(Arrays.stream(handler).
                map(DefaultChannelHandlerDelegate::new).collect(Collectors.toList())));
    }

    public static Server bind(ServerConfig serverConfig, ExchangeHandler...handler) {
        return getTransporter().bind(serverConfig, handlerWrapper(handler));
    }

    public static ClusterClientInvoker connect(ServerConfig serverConfig) {
        return getCluster().join(serverConfig.isRegisterCenter() ? new RegisterDirectory(serverConfig, handlerWrapper())
                : new StaticDirectory(serverConfig, handlerWrapper()));
    }

    public static Transporter getTransporter() {
        return ServiceLoaders.load(Transporter.class, "default");
    }

    public static Cluster getCluster() {
        return ServiceLoaders.load(Cluster.class, "default");
    }
}
