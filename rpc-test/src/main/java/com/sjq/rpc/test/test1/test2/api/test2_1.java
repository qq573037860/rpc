package com.sjq.rpc.test.test1.test2.api;

import com.sjq.rpc.domain.register.RegisterAnnotation;
import com.sjq.rpc.proxy.RpcClient;

@RpcClient(register = {
        @RegisterAnnotation(url = "http://127.0.0.1:8848", serviceName = "service_demo", type = "nacos")
})
public interface test2_1 {

    String hello(String msg);

}

