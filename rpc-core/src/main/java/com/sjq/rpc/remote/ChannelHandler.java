package com.sjq.rpc.remote;

import com.sjq.rpc.domain.RpcException;

public interface ChannelHandler {

    void received(Channel channel, Object message) throws RpcException;

}
