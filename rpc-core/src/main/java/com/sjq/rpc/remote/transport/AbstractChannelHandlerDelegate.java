package com.sjq.rpc.remote.transport;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.remote.Channel;
import com.sjq.rpc.remote.ChannelHandler;
import com.sjq.rpc.remote.RequestRunnable;
import com.sjq.rpc.support.NamedThreadFactory;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public  abstract class AbstractChannelHandlerDelegate implements ChannelHandlerDelegate {

    private ExecutorService executorService = Executors.newCachedThreadPool(new NamedThreadFactory());

    protected ChannelHandler handler;

    protected AbstractChannelHandlerDelegate(ChannelHandler handler) {
        Objects.requireNonNull(handler, "handler can not be null");
        this.handler = handler;
    }

    @Override
    public ChannelHandler getHandler() {
        return handler;
    }

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        executorService.execute(new RequestRunnable(handler, channel, message, RequestRunnable.Status.RECEIVED));
    }
}
