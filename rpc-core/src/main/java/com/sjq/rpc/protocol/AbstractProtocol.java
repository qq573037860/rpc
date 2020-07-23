package com.sjq.rpc.protocol;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.invoker.Invoker;
import com.sjq.rpc.proxy.ProxyFactory;
import com.sjq.rpc.register.Registers;
import com.sjq.rpc.remote.Server;
import com.sjq.rpc.support.proxy.ClassUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractProtocol implements Protocol {

    private static final Map<String, Object> clientProxyMap = new ConcurrentHashMap<>();

    private static final Map<String, Invoker> serverInvokerMap = new ConcurrentHashMap<>();

    protected final Map<String, Server> serverMap = new ConcurrentHashMap<>();

    private ProxyFactory proxyFactory;

    public AbstractProtocol(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Object referToProxy(Class type, ServerConfig serverConfig) throws RpcException {
        return clientProxyMap.put(ClassUtils.fullClassName(type), proxyFactory.getProxy(doReferToInvoker(type, serverConfig)));
    }

    @Override
    public <T> Invoker<T> referToInvoker(T proxy, Class<T> serviceType, ServerConfig serverConfig) throws RpcException {
        Invoker invoker = serverInvokerMap.put(ClassUtils.fullClassName(serviceType), proxyFactory.getInvoker(proxy));
        String port = String.valueOf(serverConfig.getServerPort());
        if (!serverMap.containsKey(port)) {
            synchronized (this) {
                if (!serverMap.containsKey(port)) {
                    serverMap.put(port, openServer(serverConfig));
                }
            }
        }
        if (serverConfig.isRegisterCenterForServer()) {//注册服务到注册中心
            Registers.getRegisterCenterWithRegister(serverConfig);
        }
        return invoker;
    }

    protected Invoker getServerInvoker(String key) {
        return serverInvokerMap.get(key);
    }

    @Override
    public <T>T getProxy(Class<T> cls) {
        return (T) clientProxyMap.get(cls.getName());
    }

    protected abstract <T> Invoker<T> doReferToInvoker(Class<T> type, ServerConfig serverConfig) throws RpcException;

    protected abstract Server openServer(ServerConfig serverConfig);
}
