package com.sjq.rpc.test.test1.test2.server;

import com.sjq.rpc.proxy.RpcServer;
import com.sjq.rpc.test.test1.test2.api.test2_2;
import org.springframework.stereotype.Component;

@RpcServer(serviceName = "service_demo2")
public class test2_2Impl implements test2_2 {

    @Override
    public String hello(String msg) {
        System.out.println("test2_2:服务端收到：" + msg);
        return "你也好";
    }
}
