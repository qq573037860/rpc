package com.sjq.rpc.remote;

import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.support.CallBack;


public interface Transporter {

    Server bind(ServerConfig serverConfig, ChannelHandler handler);

    Client connect(String ip, int port, ServerConfig serverConfig, ChannelHandler handler, CallBack closeCallBack);

    Client connect(String ip, int port, ServerConfig serverConfig, ChannelHandler handler);

}
