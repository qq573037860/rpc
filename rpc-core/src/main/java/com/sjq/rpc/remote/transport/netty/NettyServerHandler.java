package com.sjq.rpc.remote.transport.netty;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.remote.Channel;
import com.sjq.rpc.remote.ChannelHandler;
import com.sjq.rpc.support.IpAddressUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    static final AttributeKey<String> hostAddressAttributeKey = AttributeKey.valueOf("hostAddress");

    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    private final ChannelHandler channelHandler;

    NettyServerHandler(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    Map<String, Channel> getChannels() {
        return channels;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String hostAddress = IpAddressUtils.getSocketIpPortInfo(ctx);
        ctx.channel().attr(hostAddressAttributeKey).set(hostAddress);

        NettyChannel channel = NettyChannel.getChannel(ctx.channel());
        if (channel != null) {
            channels.put(hostAddress, channel);
        }

        logger.info("客户端[{}]连接到服务器", hostAddress);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        String hostAddress = ctx.channel().attr(hostAddressAttributeKey).get();
        try {
            channels.remove(hostAddress);
            //handler.disconnected(channel);
        } finally {
            NettyChannel.removeChannel(ctx.channel());
        }

        logger.info("客户端[{}]断开连接", hostAddress);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // server will close channel when server don't receive any heartbeat from client util timeout.
        if (evt instanceof IdleStateEvent) {
            String hostAddress = ctx.channel().attr(hostAddressAttributeKey).get();
            NettyChannel channel = NettyChannel.getChannel(ctx.channel());
            try {
                if (Objects.nonNull(channel)) {
                    logger.info("IdleStateEvent triggered, close channel[{}]" + hostAddress);
                    channel.close();
                }
            } finally {
                NettyChannel.removeChannelIfDisconnected(ctx.channel());
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof ByteBuf) {
            //try {
            //} finally {
                ReferenceCountUtil.release(msg);
            //}
        } else if (msg instanceof Request) {
            NettyChannel channel = NettyChannel.getChannel(ctx.channel());
            channelHandler.received(channel, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();

        //NettyChannel channel = NettyChannel.getChannel(ctx.channel());
        //try {
            //handler.caught(channel, cause);
        //} finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        //}

        ctx.close();
    }

}
