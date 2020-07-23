package com.sjq.rpc.remote;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.remote.transport.DefaultChannelHandlerDelegate;
import com.sjq.rpc.remote.transport.HeartbeatHandler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;
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

    public static ExchangeClientCluster connect(ServerConfig serverConfig) {
        return ExchangeClientCluster.getClient(serverConfig, handlerWrapper());
    }

    public static Transporter getTransporter() {
        Iterator<Transporter> iterator = ServiceLoader.load(Transporter.class).iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        throw new RpcException(RpcException.INTERRUPTED_EXCEPTION, "not find transporter implementation class");
    }

}
