package com.sjq.rpc.remote;

import com.sjq.rpc.domain.RpcException;

import java.util.concurrent.CompletableFuture;

public interface ExchangeHandler extends ChannelHandler {

    CompletableFuture<Object> reply(Channel channel, Object request) throws RpcException;

}
