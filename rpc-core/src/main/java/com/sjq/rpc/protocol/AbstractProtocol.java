package com.sjq.rpc.protocol;

import com.sjq.rpc.domain.register.RegisterAnnotation;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.domain.register.RegisterInfo;
import com.sjq.rpc.invoker.Invoker;
import com.sjq.rpc.proxy.ProxyFactory;
import com.sjq.rpc.proxy.RpcClient;
import com.sjq.rpc.proxy.RpcServer;
import com.sjq.rpc.register.Registers;
import com.sjq.rpc.remote.Server;
import com.sjq.rpc.support.IpAddressUtils;
import com.sjq.rpc.support.StringUtils;
import com.sjq.rpc.support.proxy.ClassUtils;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractProtocol implements Protocol {

    private static final Map<String, Object> clientProxyMap = new ConcurrentHashMap<>();

    private static final Map<String, Invoker> serverInvokerMap = new ConcurrentHashMap<>();

    private final Map<String, Server> serverMap = new ConcurrentHashMap<>();

    private final ProxyFactory proxyFactory;

    AbstractProtocol(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public <T>T referToProxy(Class<T> type, ServerConfig baseConfig) throws RpcException {
        RpcClient rpcClient = type.getAnnotation(RpcClient.class);
        if (Objects.isNull(rpcClient)) {
            throw new RpcException(RpcException.EXECUTION_EXCEPTION, "not found RpcClient of " + ClassUtils.fullClassName(type));
        }
        ServerConfig serverConfig = getConfig(rpcClient, baseConfig);
        return (T) clientProxyMap.computeIfAbsent(ClassUtils.fullClassName(type), v -> proxyFactory.getProxy(doReferToInvoker(type, serverConfig)));
    }

    @Override
    public <T> Invoker<T> referToInvoker(T proxy, Class<T> serviceType, ServerConfig baseConfig) throws RpcException {
        RpcServer rpcServer = proxy.getClass().getAnnotation(RpcServer.class);
        if (Objects.isNull(rpcServer)) {
            throw new RpcException(RpcException.EXECUTION_EXCEPTION, "not found RpcServer of " + ClassUtils.fullClassName(proxy.getClass()));
        }
        ServerConfig serverConfig = getConfig(rpcServer, baseConfig);
        Invoker<T> invoker = serverInvokerMap.computeIfAbsent(ClassUtils.fullClassName(serviceType), v -> proxyFactory.getInvoker(proxy));
        String port = String.valueOf(serverConfig.getServerPort());
        if (!serverMap.containsKey(port)) {
            synchronized (this) {
                if (!serverMap.containsKey(port)) {
                    serverMap.put(port, openServer(serverConfig));
                }
            }
        }
        if (serverConfig.isRegisterCenter()) {//注册服务到注册中心
            Registers.getRegisterCenterWithRegister(serverConfig);
        }
        return invoker;
    }

    Invoker getServerInvoker(String key) {
        return serverInvokerMap.get(key);
    }

    @Override
    public <T>T getProxy(Class<T> cls) {
        return (T) clientProxyMap.get(cls.getName());
    }

    private ServerConfig getConfig(RpcClient rpcClient, ServerConfig baseConfig) {
        ServerConfig config = baseConfig.clone();
        if (Objects.nonNull(rpcClient)) {
            RegisterAnnotation[] registerAnnotations = rpcClient.register();
            if (registerAnnotations.length > 0) {
                config.setRegister(RegisterInfo.convertToDomain(registerAnnotations[0]));
            }
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

    private ServerConfig getConfig(RpcServer rpcServer, ServerConfig baseConfig) {
        ServerConfig config = baseConfig.clone();
        if (Objects.nonNull(rpcServer)) {
            RegisterAnnotation[] registerAnnotations = rpcServer.register();
            if (registerAnnotations.length > 0) {
                config.setRegister(RegisterInfo.convertToDomain(registerAnnotations[0]));
            }
        }
        if (StringUtils.isEmpty(config.getServerIp())) {
            config.setServerIp(IpAddressUtils.getIpAddress());
        }
        return config;
    }

    protected abstract <T> Invoker<T> doReferToInvoker(Class<T> type, ServerConfig serverConfig) throws RpcException;

    protected abstract Server openServer(ServerConfig serverConfig);
}
