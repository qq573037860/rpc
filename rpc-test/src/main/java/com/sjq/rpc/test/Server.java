package com.sjq.rpc.test;

import com.sjq.rpc.RpcServerBootstrap;
import com.sjq.rpc.domain.register.RegisterInfo;

public class Server {

    public static void main(String[] args) throws InterruptedException {
        RpcServerBootstrap serverBootstrap = new RpcServerBootstrap();
        serverBootstrap.port(9999).register(new RegisterInfo("http://127.0.0.1:8848", "nacos", "service_demo")).start();
        System.out.println("RpcServerBootstrap[9999] 启动完毕");

        Server test = new Server();
        synchronized (test) {
            test.wait();
        }
    }

}
