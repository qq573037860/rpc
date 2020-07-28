package com.sjq.rpc.remote.cluster;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.remote.ExchangeClient;

import java.util.List;

public interface Directory {

    List<ExchangeClient> list(Request request);

    default ExchangeClient findWithRegister(Request request) {
        throw new RpcException("not support findWithRegister, please implement");
    }

    default boolean isRegisterSupportBalance() {
        return false;
    }

    default String getKey(String ip, int port, String serviceName) {
        return getKey(getKey(ip, port), serviceName);
    }

    default String getKey(String ip, int port) {
        return ip + ":" + port;
    }

    default String getKey(String ipPortInfo, String registerServiceName) {
        return ipPortInfo + "/" + registerServiceName;
    }

}
