package com.sjq.rpc.remote.transport;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.Result;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.RpcResult;
import com.sjq.rpc.remote.Channel;
import com.sjq.rpc.remote.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HeartbeatHandler extends AbstractChannelHandlerDelegate {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

    public HeartbeatHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public void received(Channel channel, Object message) throws RpcException {
        if (isHeartbeatRequest(message)) {
            Request request = (Request) message;
            Result result = new RpcResult(request.getId(), request.isHeartbeat());
            channel.send(result, true);
            if (logger.isDebugEnabled()) {
                logger.debug("Received heartbeat from remote channel[{}] , cause: The channel has no data-transmission exceeds a heartbeat period", channel.getHostAddress());
            }
        } else if (isHeartbeatResult(message)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Receive remote channel[{}] heartbeat response in thread {}", channel.getHostAddress(), Thread.currentThread().getName());
            }
        } else {
            handler.received(channel, message);
        }
    }

    private boolean isHeartbeatRequest(Object message) {
        return message instanceof Request && ((Request)message).isHeartbeat();
    }

    private boolean isHeartbeatResult(Object message) {
        return message instanceof Result && ((Result)message).isHeartbeat();
    }
}
