package com.sjq.rpc.test.test1.test2.server;

import com.sjq.rpc.proxy.RpcServer;
import com.sjq.rpc.test.test1.test2.api.test2_1;

@RpcServer(serviceName = "service_demo")
public class test2_1Impl implements test2_1 {

    @Override
    public String hello(String msg) {
        System.out.println("test2_1:服务端收到：" + msg);
        return "你也好";
    }
}
