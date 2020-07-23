package com.sjq.rpc;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.protocol.DefaultProtocol;
import com.sjq.rpc.protocol.Protocol;
import com.sjq.rpc.proxy.JavassistProxyFactory;
import com.sjq.rpc.proxy.RpcClient;
import com.sjq.rpc.support.PackageScanner;
import com.sjq.rpc.support.StringUtils;

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
        PackageScanner.scanInterfaceByPackagePathAndAnnotaion(scanPage, new Class[]{RpcClient.class})
            .stream().forEach(cls -> {

            //config
            RpcClient rpcClient = (RpcClient) cls.getAnnotation(RpcClient.class);
            ServerConfig config = getConfig(rpcClient);

            //referToProxy
            protocol.referToProxy(cls, config);
        });
    }

    private ServerConfig getConfig(RpcClient rpcClient) {
        ServerConfig config = (ServerConfig) serverConfig.clone();
        if (Objects.nonNull(rpcClient)) {
            config.setRegisterServiceName(rpcClient.serviceName());
            if (StringUtils.isNotEmpty(rpcClient.serverUrl())) {
                try {
                    config.setServerUrl(rpcClient.serverUrl());
                } catch (Exception e) {
                    throw new RpcException(e);
                }
            }
            if (rpcClient.requestTimeout() > 0) {
                config.setRequestTimeout(rpcClient.requestTimeout());
            }
        }
        return config;
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
