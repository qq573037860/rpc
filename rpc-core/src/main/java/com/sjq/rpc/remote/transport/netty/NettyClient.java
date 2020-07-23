package com.sjq.rpc.remote.transport.netty;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.remote.transport.AbstractClient;
import com.sjq.rpc.support.CallBack;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.sjq.rpc.remote.transport.netty.NettyEventLoopFactory.eventLoopGroup;
import static com.sjq.rpc.remote.transport.netty.NettyEventLoopFactory.socketChannelClass;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class NettyClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private Bootstrap bootstrap;
    private static final EventLoopGroup WORK_GROUP = eventLoopGroup(1, "NettyClientWorker");
    private volatile io.netty.channel.Channel channel;

    public NettyClient(String ip, int port, int connectTimeout, int heartbeatTimeout, com.sjq.rpc.remote.ChannelHandler handler, CallBack closeCallBack) {
        super(ip, port, connectTimeout, heartbeatTimeout, handler, closeCallBack);
    }

    @Override
    protected void doOpen() {
        final NettyClient thisInstance = this;
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(WORK_GROUP)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectTimeout())
                .channel(socketChannelClass())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast("decoder", new NettyDecoder())
                                    .addLast("encoder", new NettyEncoder())
                                    .addLast("client-idle-handler", new IdleStateHandler(getHeartbeatTimeout(), 0, 0, MILLISECONDS))
                                    .addLast("handler", new NettyClientHandler(thisInstance));
                    }
                });
    }

    @Override
    protected void tryConnect() {
        long start = System.currentTimeMillis();
        ChannelFuture future = bootstrap.connect(getIp(), getPort());
        try {
            boolean ret = future.awaitUninterruptibly(getConnectTimeout(), MILLISECONDS);

            if (ret && future.isSuccess()) {
                Channel newChannel = future.channel();
                try {
                    // Close old channel
                    // copy reference
                    Channel oldChannel = this.channel;
                    if (oldChannel != null) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close old netty channel " + oldChannel + " on create new netty channel " + newChannel);
                            }
                            oldChannel.close();
                        } finally {
                            NettyChannel.removeChannelIfDisconnected(oldChannel);
                        }
                    }
                } finally {
                    if (isClosed()) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close new netty channel " + newChannel + ", because the client closed.");
                            }
                            newChannel.close();
                        } finally {
                            this.channel = null;
                            NettyChannel.removeChannelIfDisconnected(newChannel);
                        }
                    } else {
                        this.channel = newChannel;
                    }
                }
            } else if (future.cause() != null) {
                throw new RpcException(RpcException.NETWORK_EXCEPTION, "client(url: " + future.channel().remoteAddress() + ") failed to connect to server "
                        + getHostAddress() + ", error message is:" + future.cause().getMessage(), future.cause());
            } else {
                throw new RpcException(RpcException.NETWORK_EXCEPTION, "client(url: " + future.channel().remoteAddress() + ") failed to connect to server "
                        + getHostAddress() + " client-side timeout "
                        + getConnectTimeout() + "ms (elapsed: " + (System.currentTimeMillis() - start) + "ms) from netty client["
                        + future.channel().remoteAddress() + "]");
            }
        } finally {
            // just add new valid channel to NettyChannel's cache
            if (!isConnected()) {
                //future.cancel(true);
            }
        }
        future.channel().closeFuture().addListener(future1 -> logger.info("nettyClient is closed"));
        logger.info("nettyClient starts successfully");
    }

    @Override
    protected com.sjq.rpc.remote.Channel getChannel() {
        return Objects.nonNull(this.channel) && this.channel.isOpen() ? NettyChannel.getChannel(this.channel) : null;
    }

    @Override
    protected void doClose() {
        if (Objects.nonNull(this.channel)) {
            this.channel.close();
        }
        // can't shutdown nioEventLoopGroup because the method will be invoked when closing one channel but not a client,
        // but when and how to close the nioEventLoopGroup ?
        // nioEventLoopGroup.shutdownGracefully();
    }
}
