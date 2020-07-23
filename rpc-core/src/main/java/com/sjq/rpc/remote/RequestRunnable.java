package com.sjq.rpc.remote;

public class RequestRunnable implements Runnable {

    private final ChannelHandler handler;
    private final Channel channel;
    private final Object message;
    private final Status status;

    public RequestRunnable(ChannelHandler handler, Channel channel, Object message, Status status) {
        this.handler = handler;
        this.channel = channel;
        this.message = message;
        this.status = status;
    }

    @Override
    public void run() {
        switch (status) {
            case RECEIVED:
                handler.received(channel, message);
                break;
        }
    }

    public enum Status {

        RECEIVED

    }

}
