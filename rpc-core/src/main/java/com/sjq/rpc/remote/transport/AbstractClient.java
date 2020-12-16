package com.sjq.rpc.remote.transport;

import com.sjq.rpc.domain.Constants;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.remote.Channel;
import com.sjq.rpc.remote.ChannelHandler;
import com.sjq.rpc.remote.Client;
import com.sjq.rpc.support.CallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public abstract class AbstractClient implements Client, ChannelHandler {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    private String ip;
    private int port;
    private int connectTimeout;
    private int heartbeatTimeout;
    private volatile boolean closed;
    private final ChannelHandler handler;
    private final CallBack closeCallBack;

    private int retryTime = Constants.CLIENT_RETRY_TIME;

    public AbstractClient(String ip, int port, int connectTimeout, int heartbeatTimeout, ChannelHandler handler, CallBack closeCallBack) {
        Objects.requireNonNull(handler);

        this.ip = ip;
        this.port = port;
        this.connectTimeout = connectTimeout;
        this.heartbeatTimeout = heartbeatTimeout;
        this.handler = handler;
        this.closeCallBack = closeCallBack;

        doOpen();
        connect();
    }

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        handler.received(channel, message);
    }

    public void connect() {
        if (!isConnected()) {
            synchronized (this) {
                if (!isConnected()) {
                    closed = false;
                    doConnectWithRetry();
                }
            }
        }
    }

    private void doConnectWithRetry() {
        if (retryTime > 0) {
            retryTime--;
            try {
                tryConnect();
                retryTime = Constants.CLIENT_RETRY_TIME;
            } catch (Exception e) {
                if (retryTime > 0) {
                    try {
                        Thread.sleep(Constants.CLIENT_RETRY_INTERVAL);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                connect();
            }
        } else {
            logger.warn("fail to connect,retryTime is zero");
            close();
            if (Objects.nonNull(closeCallBack)) {
                closeCallBack.apply();
            }
        }
    }

    @Override
    public boolean isConnected() {
        Channel channel = this.getChannel();
        return Objects.nonNull(channel) && channel.isConnected();
    }

    @Override
    public boolean isActive() {
        Channel channel = this.getChannel();
        return !isClosed() && Objects.nonNull(channel) && channel.isActive();
    }

    protected boolean isClosed() {
        return closed;
    }

    @Override
    public void close() {
        closed = true;
        doClose();
    }

    @Override
    public void send(Object data, boolean isLast) throws RpcException {
        Channel channel = getChannel();
        if (Objects.nonNull(channel)) {
            channel.send(data, isLast);
        }
    }

    protected abstract Channel getChannel();

    protected abstract void doOpen();

    protected abstract void tryConnect();

    protected abstract void doClose();

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public String getHostAddress() {
        return ip + ":" + port;
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }
}
