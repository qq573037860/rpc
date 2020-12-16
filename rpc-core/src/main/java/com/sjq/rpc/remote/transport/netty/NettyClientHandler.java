package com.sjq.rpc.remote.transport.netty;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcRequest;
import com.sjq.rpc.remote.ChannelHandler;
import com.sjq.rpc.remote.transport.AbstractClient;
import com.sjq.rpc.support.IpAddressUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * NettyClientHandler
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    public static final AttributeKey<String> hostAddressAttributeKey = AttributeKey.valueOf("hostAddress");

    private final ChannelHandler handler;

    public NettyClientHandler(ChannelHandler handler) {
        Objects.requireNonNull(handler, "ChannelHandler can not be null");

        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        String hostAddress = IpAddressUtils.getSocketIpPortInfo(ctx);
        ctx.channel().attr(hostAddressAttributeKey).set(hostAddress);
        //NettyChannel channel = NettyChannel.getChannel(ctx.channel());

        logger.info("连接到服务器[{}]", hostAddress);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //NettyChannel channel = NettyChannel.getChannel(ctx.channel());
        //try {
            //handler.disconnected(channel);
        //} finally {
            NettyChannel.removeChannel(ctx.channel());
        //}

        if (logger.isInfoEnabled()) {
            logger.info("chanel[{}] disconnects from server, try to reConnect ···", ctx.channel().attr(hostAddressAttributeKey).get());
        }
        ((AbstractClient)handler).connect();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyChannel channel = NettyChannel.getChannel(ctx.channel());
        handler.received(channel, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // send heartbeat when read idle.
        if (evt instanceof IdleStateEvent) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("IdleStateEvent[{}] triggers, channel[{}]", ((IdleStateEvent)evt).state(), ctx.channel().attr(hostAddressAttributeKey).get());
                }
                //发送心跳
                NettyChannel channel = NettyChannel.getChannel(ctx.channel());
                if (Objects.nonNull(channel)) {
                    Request request = new RpcRequest(true);
                    channel.send(request, true);
                }
            } finally {
                NettyChannel.removeChannelIfDisconnected(ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.error("exceptionCaught", cause);
        //NettyChannel channel = NettyChannel.getChannel(ctx.channel());
        //try {
            //handler.caught(channel, cause);
        //} finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        //}
    }

}
