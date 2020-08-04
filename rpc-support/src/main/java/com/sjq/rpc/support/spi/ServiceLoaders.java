package com.sjq.rpc.support.spi;

import com.sjq.rpc.domain.RpcException;

import java.util.Iterator;

public class ServiceLoaders {

    public static <T>T load(Class<T> cls, String clsKey) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(cls, clsKey);
        Iterator<T> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        throw new RpcException(RpcException.INTERRUPTED_EXCEPTION, String.format("not find %s implementation class", cls.getSimpleName()));
    }
}
