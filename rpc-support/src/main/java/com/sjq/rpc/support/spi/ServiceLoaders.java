package com.sjq.rpc.support.spi;

import com.sjq.rpc.domain.RpcException;

public class ServiceLoaders {

    public static <T>T load(Class<T> cls, String clsKey) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(cls, clsKey);
        for (T t : serviceLoader) {
            return t;
        }
        throw new RpcException(RpcException.INTERRUPTED_EXCEPTION, String.format("not find %s implementation class", cls.getSimpleName()));
    }
}
