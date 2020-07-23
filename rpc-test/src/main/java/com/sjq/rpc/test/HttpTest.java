package com.sjq.rpc.test;

import com.sjq.rpc.test.test1.test2.server.test2_1Impl;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class HttpTest {

    public static void main(String[] args) {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8001), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.createContext("/hello", exchange -> {
            InputStream in = exchange.getRequestBody();
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            in.close();
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            os.write(new test2_1Impl().hello(new String(bytes)).getBytes("utf-8"));
            os.close();
        });
        server.start();
    }

}
