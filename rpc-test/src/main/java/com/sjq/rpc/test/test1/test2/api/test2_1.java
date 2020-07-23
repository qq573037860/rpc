package com.sjq.rpc.test.test1.test2.api;

import com.sjq.rpc.proxy.RpcClient;

@RpcClient(serverUrl = "register://127.0.0.1:8848", serviceName = "service_demo")
public interface test2_1 {

    String hello(String msg);

}

