package com.sjq.rpc.test;

import com.sjq.rpc.RpcServerBootstrap;
import com.sjq.rpc.domain.register.RegisterInfo;

public class Server2 {

    public static void main(String[] args) throws InterruptedException {
        RpcServerBootstrap serverBootstrap = new RpcServerBootstrap();
        serverBootstrap.port(9998).register(new RegisterInfo("http://127.0.0.1:8848", "nacos", "service_demo2")).start();
        System.out.println("RpcServerBootstrap[9998] 启动完毕");

        Server2 test = new Server2();
        synchronized (test) {
            test.wait();
        }
    }

}
