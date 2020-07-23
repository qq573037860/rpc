package com.sjq.rpc.remote.transport.netty;

import com.sjq.rpc.domain.RpcException;
import io.netty.channel.Channel;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class NettyChannel implements com.sjq.rpc.remote.Channel {

    private Channel channel;
    private String hostAddress;

    private static final ConcurrentHashMap<Channel, NettyChannel> channelMap = new ConcurrentHashMap<>();

    private NettyChannel(Channel channel) {
        Objects.requireNonNull(channel);

        this.channel = channel;
        this.hostAddress = channel.attr(NettyServerHandler.hostAddressAttributeKey).get();
    }

    public static NettyChannel getChannel(Channel channel) {
        if (Objects.nonNull(channel) && channel.isOpen()) {
            return channelMap.computeIfAbsent(channel, v -> new NettyChannel(channel));
        }
        return null;
    }


    public static void removeChannel(Channel channel) {
        channelMap.remove(channel);
    }

    static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isActive()) {
            channelMap.remove(ch);
        }
    }

    @Override
    public void close() {
        removeChannel(this.channel);
        this.channel.close();
    }

    @Override
    public void send(Object data, boolean isLast) throws RpcException {
        if (this.channel.isOpen()) {
            if (isLast) {
                this.channel.writeAndFlush(data);
            } else {
                this.channel.write(data);
            }
        }
    }

    @Override
    public boolean isConnected() {
        return this.channel.isOpen();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public String getHostAddress() {
        return this.hostAddress;
    }

}
