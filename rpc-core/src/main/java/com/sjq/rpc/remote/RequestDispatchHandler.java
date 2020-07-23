package com.sjq.rpc.remote;

import com.sjq.rpc.domain.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

public class RequestDispatchHandler implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestDispatchHandler.class);

    private final Collection<ChannelHandler> channelHandlers = new CopyOnWriteArraySet<ChannelHandler>();

    public RequestDispatchHandler(ChannelHandler... handlers) {
        this(handlers == null ? null : Arrays.asList(handlers));
    }

    public RequestDispatchHandler(Collection<ChannelHandler> handlers) {
        if (null != handlers && !handlers.isEmpty()) {
            this.channelHandlers.addAll(handlers);
        }
    }

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        for (ChannelHandler handler : channelHandlers) {
            try {
                handler.received(channel, message);
            } catch (Throwable t) {
                logger.error(t.getMessage(), t);
            }
        }
    }

}
