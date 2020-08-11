package com.sjq.rpc.test.test1.test2.server;

import com.sjq.rpc.domain.register.RegisterAnnotation;
import com.sjq.rpc.proxy.RpcServer;
import com.sjq.rpc.test.test1.test2.api.test2_2;

@RpcServer(register =
        {@RegisterAnnotation(url = "http://127.0.0.1:8848", serviceName = "service_demo2", type = "nacos")
})
public class test2_2Impl implements test2_2 {

    @Override
    public String hello(String msg) {
        System.out.println("test2_2:服务端收到：" + msg);
        return "你也好";
    }

}
