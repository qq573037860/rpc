package com.sjq.rpc.remote;

import com.sjq.rpc.domain.Request;

public interface ExchangeClient extends Client {

    DefaultFuture request(Request request, int timeout);

}
