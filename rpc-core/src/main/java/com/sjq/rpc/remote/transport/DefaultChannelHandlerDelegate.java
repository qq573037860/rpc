package com.sjq.rpc.remote.transport;

import com.sjq.rpc.domain.*;
import com.sjq.rpc.remote.Channel;
import com.sjq.rpc.remote.ChannelHandler;
import com.sjq.rpc.remote.DefaultFuture;
import com.sjq.rpc.remote.ExchangeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

public class DefaultChannelHandlerDelegate implements ChannelHandlerDelegate {

    private static final Logger logger = LoggerFactory.getLogger(DefaultChannelHandlerDelegate.class);

    private ExchangeHandler handler;

    public DefaultChannelHandlerDelegate(){}

    public DefaultChannelHandlerDelegate(ExchangeHandler handler) {
        this.handler = handler;
    }

    @Override
    public ChannelHandler getHandler() {
        return handler;
    }

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        if (message instanceof Request) {
            // handle request.
            Request request = (Request) message;
            handleRequest(channel, request);
        } else if (message instanceof Result) {
            handleResponse(channel, (Result) message);
        }
    }

    void handleRequest(final Channel channel, Request req) throws RpcException {
        Result res = new RpcResult(req.getId());
        try {
            CompletionStage<Object> future = handler.reply(channel, req);
            future.whenComplete((r, e) -> {
                if (e == null) {
                    res.setResult(r);
                } else {
                    res.setException(e);
                }
                try {
                    channel.send(res, true);
                } catch (RpcException ex) {
                    logger.error("Send result to client[{}] failed", channel.getHostAddress(), ex);
                }
            });
        } catch (Throwable e) {
            logger.error("handleRequest failed", e);
            res.setException(e);
            channel.send(res, true);
        }
    }

    /**
     * 不需要handler
     * @param channel
     * @param result
     * @throws RpcException
     */
    void handleResponse(Channel channel, Result result) throws RpcException {
        if (result != null) {
            DefaultFuture.received(channel, result, false);
        }
    }
}
