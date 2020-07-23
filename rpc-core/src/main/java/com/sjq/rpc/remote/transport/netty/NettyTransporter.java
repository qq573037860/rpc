package com.sjq.rpc.remote.transport.netty;

import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.remote.*;
import com.sjq.rpc.support.CallBack;


public class NettyTransporter implements Transporter {

    @Override
    public Server bind(ServerConfig serverConfig, ChannelHandler handler) {
        return new NettyServer(serverConfig.getServerPort(), serverConfig.getHeartbeatTimeout(), handler);
    }

    @Override
    public Client connect(String ip, int port, ServerConfig serverConfig, ChannelHandler handler, CallBack closeCallBack) {
        return new NettyClient(ip, port, serverConfig.getConnectTimeout(), serverConfig.getHeartbeatTimeout(), handler, closeCallBack);
    }

    @Override
    public Client connect(String ip, int port, ServerConfig serverConfig, ChannelHandler handler) {
        return new NettyClient(ip, port, serverConfig.getConnectTimeout(), serverConfig.getHeartbeatTimeout(), handler, null);
    }
}
