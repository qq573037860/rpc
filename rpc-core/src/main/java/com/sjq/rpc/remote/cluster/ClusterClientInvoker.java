package com.sjq.rpc.remote.cluster;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.remote.DefaultFuture;

public interface ClusterClientInvoker {

    DefaultFuture request(Request request, int timeout);

}
