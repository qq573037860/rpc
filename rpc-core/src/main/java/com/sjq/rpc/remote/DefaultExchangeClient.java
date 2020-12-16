package com.sjq.rpc.remote;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.RpcException;

import java.util.Objects;

public class DefaultExchangeClient extends AbstractExchangeClient {

    private final Client client;
    private final String hostAddress;

    public DefaultExchangeClient(Client client) {
        this.client = client;
        this.hostAddress = client.getHostAddress();
    }

    @Override
    protected DefaultFuture doRequest(Request request, int timeout) throws RpcException {
        DefaultFuture defaultFuture = new DefaultFuture(client, request, timeout);
        try {
            client.send(request, true);
        } catch (Exception e) {
            defaultFuture.cancel(true);
            throw new RpcException(e);
        }
        return defaultFuture;
    }

    @Override
    public void close() {
        this.client.close();
    }

    @Override
    public void send(Object data, boolean isLast) throws RpcException {
        this.client.send(data, isLast);
    }

    @Override
    public boolean isConnected() {
        return this.client.isConnected();
    }

    @Override
    public boolean isActive() {
        return this.client.isActive();
    }

    @Override
    public String getHostAddress() {
        return client.getHostAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultExchangeClient that = (DefaultExchangeClient) o;
        return Objects.equals(hostAddress, that.hostAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostAddress);
    }
}
