package com.sjq.rpc.remote.transport.netty;

import com.sjq.rpc.support.codec.ObjectSerialize;
import com.sjq.rpc.support.codec.ProtostuffSerialize;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class NettyDecoder extends ReplayingDecoder {

    private final ObjectSerialize serialize = new ProtostuffSerialize();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //decode header
        int headerLength = in.readInt();
        byte[] header = new byte[headerLength];
        in.readBytes(header);
        //decode body
        int bodyLength = in.readInt();
        byte[] body = new byte[bodyLength];
        in.readBytes(body);

        Class clazz = Class.forName(new String(header));
        out.add(serialize.deserialize(body, clazz));
    }
}
