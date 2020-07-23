package com.sjq.rpc.remote.transport;

import com.sjq.rpc.remote.ChannelHandler;

public interface ChannelHandlerDelegate extends ChannelHandler {

    ChannelHandler getHandler();

}
