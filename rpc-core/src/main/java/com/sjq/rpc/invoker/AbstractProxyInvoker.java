/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sjq.rpc.invoker;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.Result;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * InvokerWrapper
 */
public abstract class AbstractProxyInvoker<T> implements Invoker<T> {
    Logger logger = LoggerFactory.getLogger(AbstractProxyInvoker.class);

    private final T proxy;

    public AbstractProxyInvoker(T proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("proxy == null");
        }
        this.proxy = proxy;
    }

    @Override
    public Class<T> getInterfaceType() {
        return (Class<T>) proxy.getClass();
    }

    @Override
    public Object invoke(Request request) throws RpcException {
        try {
            Object value = doInvoke(proxy, request.getMethodName(), request.getParameterTypes(), request.getParameters());

			//CompletableFuture<Object> future = wrapWithFuture(value);
            /*CompletableFuture<AppResponse> appResponseFuture = future.handle((obj, t) -> {
                AppResponse result = new AppResponse();
                if (t != null) {
                    if (t instanceof CompletionException) {
                        result.setException(t.getCause());
                    } else {
                        result.setException(t);
                    }
                } else {
                    result.setValue(obj);
                }
                return result;
            });*/
            return value;
        } /*catch (InvocationTargetException e) {
            if (RpcContext.getContext().isAsyncStarted() && !RpcContext.getContext().stopAsync()) {
                logger.error("Provider async started, but got an exception from the original method, cannot write the exception back to consumer because an async result may have returned the new thread.", e);
            }
            return AsyncRpcResult.newDefaultAsyncResult(null, e.getTargetException(), invocation);
        } */catch (Throwable e) {
            throw new RpcException("Failed to invoke proxy method " + request.getMethodName() + ", cause: " + e.getMessage(), e);
        }
    }

	private CompletableFuture<Object> wrapWithFuture(Object value) {
        /*if (RpcContext.getContext().isAsyncStarted()) {
            return ((AsyncContextImpl)(RpcContext.getContext().getAsyncContext())).getInternalFuture();
        } else */if (value instanceof CompletableFuture) {
            return (CompletableFuture<Object>) value;
        }
        return CompletableFuture.completedFuture(value);
    }

    protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws Throwable;

}
