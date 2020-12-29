package com.sjq.rpc;

import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.protocol.DefaultProtocol;
import com.sjq.rpc.protocol.Protocol;
import com.sjq.rpc.proxy.JavassistProxyFactory;
import com.sjq.rpc.proxy.RpcClient;
import com.sjq.rpc.support.PackageScanner;

import java.util.Objects;

public class RpcBootstrap {

    private String scanPage;
    private ServerConfig serverConfig;
    private Protocol protocol;

    public void start() {
        initDefault();
        registerClass();
    }

    private void initDefault() {
        if (Objects.isNull(serverConfig)) {
            serverConfig = new ServerConfig();
        }
        if (Objects.isNull(scanPage)) {
            scanPage = "";
        }
        if (Objects.isNull(protocol)) {
            protocol = new DefaultProtocol(new JavassistProxyFactory());
        }
    }

    private void registerClass() {
        PackageScanner.scanInterfaceByPackagePathAndAnnotation(scanPage, new Class[]{RpcClient.class}).forEach(cls -> {
            //referToProxy
            protocol.referToProxy(cls, serverConfig);
        });
    }

    public RpcBootstrap scanPage(String scanPage) {
        this.scanPage = scanPage;
        return this;
    }

    public RpcBootstrap serverConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public <T>T getBean(Class<T> cls) {
        return protocol.getProxy(cls);
    }
}
