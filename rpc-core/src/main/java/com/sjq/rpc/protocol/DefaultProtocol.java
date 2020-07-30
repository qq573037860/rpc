package com.sjq.rpc.protocol;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.invoker.DefaultInvoker;
import com.sjq.rpc.invoker.Invoker;
import com.sjq.rpc.proxy.ProxyFactory;
import com.sjq.rpc.remote.*;
import com.sjq.rpc.remote.Transporters;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultProtocol extends AbstractProtocol {

    private ExchangeHandler exchangeHandler = new ExchangeHandler() {

        @Override
        public CompletableFuture<Object> reply(Channel channel, Object obj) throws RpcException {
            if (!(obj instanceof Request)) {
                throw new RpcException(RpcException.INTERRUPTED_EXCEPTION, "obj type is not correct");
            }

            Request request = (Request) obj;
            Invoker invoker = getServerInvoker(request.getInterfaceServiceFullName());
            if (Objects.isNull(invoker)) {
                throw  new RpcException(RpcException.EXECUTION_EXCEPTION, "no invoker founded");
            }
            return CompletableFuture.completedFuture(invoker.invoke(request));
        }

        @Override
        public void received(Channel channel, Object message) throws RpcException {

        }
    };

    public DefaultProtocol(ProxyFactory proxyFactory) {
        super(proxyFactory);
    }

    @Override
    protected <T> Invoker<T> doReferToInvoker(Class<T> type, ServerConfig serverConfig) throws RpcException {
        return new DefaultInvoker<>(type, Transporters.connect(serverConfig),
                serverConfig.getRequestTimeout(), null);
    }

    @Override
    protected Server openServer(ServerConfig serverConfig){
        return Transporters.bind(serverConfig, exchangeHandler);
    }
}
