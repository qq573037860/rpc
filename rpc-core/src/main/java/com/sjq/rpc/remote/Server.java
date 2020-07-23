package com.sjq.rpc.remote;

import java.util.Collection;

public interface Server {

    void close();

    void received(Channel channel, Object msg);

    /**
     * get channels.
     *
     * @return channels
     */
    Collection<Channel> getChannels();

}
