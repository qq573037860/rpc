package com.sjq.rpc.remote.transport;

import com.sjq.rpc.domain.Constants;
import com.sjq.rpc.remote.Channel;
import com.sjq.rpc.remote.ChannelHandler;
import com.sjq.rpc.remote.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public abstract class AbstractServer implements ChannelHandler, Server {

    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);

    private int port;
    private int heartBeatTimeout;
    private ChannelHandler channelHandler;

    public AbstractServer (int port, int heartBeatTimeout, ChannelHandler channelHandler) {
        Objects.requireNonNull(channelHandler, "ChannelHandler can not be null");

        this.port = port == 0 ? Constants.DEFAULT_SERVER_PORT : port;
        this.channelHandler = channelHandler;
        this.heartBeatTimeout = heartBeatTimeout == 0 ? Constants.DEFAULT_HEARTBEAT : heartBeatTimeout;

        doOpen();
    }

    @Override
    public void close() {
        doClose();
    }

    @Override
    public void received(Channel channel, Object msg) {
        channelHandler.received(channel, msg);
    }

    protected abstract void doOpen();

    protected abstract void doClose();

    public int getPort() {
        return port;
    }

    public int getHeartBeatTimeout() {
        return heartBeatTimeout;
    }
}
