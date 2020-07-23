package com.sjq.rpc.register;

import com.alibaba.nacos.api.naming.listener.Event;

public interface RegisterListener {

    void onEvent(Event e);

}
