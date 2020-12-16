package com.sjq.rpc.remote;

import com.sjq.rpc.domain.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestDispatchHandler implements ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestDispatchHandler.class);

    private final Set<ChannelHandler> channelHandlers = ConcurrentHashMap.newKeySet();

    RequestDispatchHandler(ChannelHandler... handlers) {
        this(Objects.isNull(handlers) || handlers.length == 0 ? null : Arrays.asList(handlers));
    }

    RequestDispatchHandler(List<ChannelHandler> handlers) {
        if (!CollectionUtils.isEmpty(handlers)) {
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
