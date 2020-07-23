package com.sjq.rpc.test;

import com.sjq.rpc.RpcServerBootstrap;

public class TestServer2 {

    public static void main(String[] args) throws InterruptedException {
        RpcServerBootstrap serverBootstrap = new RpcServerBootstrap();
        serverBootstrap.port(9998).registerCenterUrl("http://127.0.0.1:8848").start();
        System.out.println("RpcServerBootstrap[9998] 启动完毕");

        TestServer2 test = new TestServer2();
        synchronized (test) {
            test.wait();
        }
    }

}
