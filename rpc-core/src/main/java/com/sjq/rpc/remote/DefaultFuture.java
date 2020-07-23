package com.sjq.rpc.remote;

import com.sjq.rpc.domain.Request;
import com.sjq.rpc.domain.Result;
import com.sjq.rpc.domain.RpcException;
import com.sjq.rpc.domain.RpcResult;
import com.sjq.rpc.support.NamedThreadFactory;
import com.sjq.rpc.support.timer.HashedWheelTimer;
import com.sjq.rpc.support.timer.Timeout;
import com.sjq.rpc.support.timer.Timer;
import com.sjq.rpc.support.timer.TimerTask;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DefaultFuture extends CompletableFuture<Result> {

    private Channel channel;
    private Request request;
    private long id;
    private int timeout;

    private Timeout timeoutCheckTask;

    private static final Map<Long, Channel> CHANNELS = new ConcurrentHashMap<>();
    private static final Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    public static final Timer TIME_OUT_TIMER = new HashedWheelTimer(
            new NamedThreadFactory("dubbo-future-timeout", true),
            30,
            TimeUnit.MILLISECONDS);

    public DefaultFuture(Channel channel, Request request, int timeout) {
        this.channel = channel;
        this.request = request;
        this.id = request.getId();
        this.timeout = timeout;

        FUTURES.put(id, this);
        CHANNELS.put(id, channel);

        timeoutCheck(this);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        RpcResult result = new RpcResult(id, null,
                new RpcException(RpcException.NETWORK_EXCEPTION, "request future has been canceled."));
        this.doReceived(result);
        FUTURES.remove(id);
        CHANNELS.remove(id);
        return true;
    }

    public static void received(Channel channel, Result response, boolean timeout) {
        try {
            DefaultFuture future = FUTURES.remove(response.getId());
            if (future != null) {
                Timeout t = future.timeoutCheckTask;
                if (!timeout) {
                    // decrease Time
                    t.cancel();
                }
                future.doReceived(response);
            } else {
                //log.error()
            }
        } finally {
            CHANNELS.remove(response.getId());
        }
    }

    public static DefaultFuture getFuture(Long requestId) {
        return FUTURES.get(requestId);
    }

    public long getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    public Request getRequest() {
        return request;
    }

    public int getTimeout() {
        return timeout;
    }

    private void doReceived(Result res) {
        if(Objects.isNull(res))
            throw new RpcException(RpcException.INVALID_ARGUMENT_EXCEPTION, "RpcResult cannot be null");
        super.complete(res);
    }

    /**
     * check time out of the future
     */
    private static void timeoutCheck(DefaultFuture future) {
        TimeoutCheckTask task = new TimeoutCheckTask(future.getId());
        future.timeoutCheckTask = TIME_OUT_TIMER.newTimeout(task, future.getTimeout(), TimeUnit.MILLISECONDS);
    }

    private static class TimeoutCheckTask implements TimerTask {

        private final Long requestID;

        TimeoutCheckTask(Long requestID) {
            this.requestID = requestID;
        }

        @Override
        public void run(Timeout timeout) {
            DefaultFuture future = DefaultFuture.getFuture(requestID);
            if (future == null || future.isDone()) {
                return;
            }

            /*if (future.getExecutor() != null) {//使用线程池处理
                future.getExecutor().execute(() -> notifyTimeout(future));
            } else {*/
                notifyTimeout(future);
            //}
        }

        private void notifyTimeout(DefaultFuture future) {
            Result timeoutResponse = new RpcResult(future.getId(), null,
                    new RpcException(RpcException.TIMEOUT_EXCEPTION, String.format("method[%s] is time out", future.getRequest().getMethodName())));
            DefaultFuture.received(future.getChannel(), timeoutResponse, true);
        }
    }
}
