package com.sjq.rpc.test;

import com.sjq.rpc.RpcServerBootstrap;

public class TestServer1 {

    public static void main(String[] args) throws InterruptedException {
        RpcServerBootstrap serverBootstrap = new RpcServerBootstrap();
        serverBootstrap.port(9999).registerCenterUrl("http://127.0.0.1:8848").start();
        System.out.println("RpcServerBootstrap[9999] 启动完毕");

        TestServer1 test = new TestServer1();
        synchronized (test) {
            test.wait();
        }
    }

}
