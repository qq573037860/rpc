package com.sjq.rpc.invoker;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;

public interface Invoker<T> {

    Class<T> getInterfaceType();

    Object invoke(Request request) throws RpcException;

}
