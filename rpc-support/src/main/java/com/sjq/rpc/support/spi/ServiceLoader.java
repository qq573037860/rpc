package com.sjq.rpc.support.spi;

import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.support.proxy.ClassUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceLoader {

    private static final Map<String, java.util.ServiceLoader> SERVICE_LOADER_MAP = new ConcurrentHashMap<>();

    public static <T>T load(Class<T> cls) {
        java.util.ServiceLoader<T> serviceLoader = SERVICE_LOADER_MAP.computeIfAbsent(ClassUtils.fullClassName(cls), v -> java.util.ServiceLoader.load(cls));
        Iterator<T> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        throw new RpcException(RpcException.INTERRUPTED_EXCEPTION, String.format("not find %s implementation class", cls.getSimpleName()));
    }
}
