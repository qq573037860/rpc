package com.sjq.rpc.remote;

import com.sjq.rpc.domain.RpcException;

public interface Channel {

    void close();

    void send(Object data, boolean isLast) throws RpcException;

    boolean isConnected();

    boolean isActive();

    String getHostAddress();

}
