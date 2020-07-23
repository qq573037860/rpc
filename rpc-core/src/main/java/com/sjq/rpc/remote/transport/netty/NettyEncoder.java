package com.sjq.rpc.remote.transport.netty;

import com.sjq.rpc.support.codec.ObjectSerialize;
import com.sjq.rpc.support.codec.ProtostuffSerialize;
import com.sjq.rpc.support.proxy.ClassUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder {

    private ObjectSerialize serialize = new ProtostuffSerialize();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] body = serialize.serialize(msg);
        byte[] header = ClassUtils.fullClassName(msg.getClass()).getBytes();
        int headerLength = header.length;
        int bodyLength = body.length;

        out.writeInt(headerLength);
        out.writeBytes(header);
        out.writeInt(bodyLength);
        out.writeBytes(body);
    }
}
