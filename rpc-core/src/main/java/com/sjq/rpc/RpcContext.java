package com.sjq.rpc;

import com.sjq.rpc.support.ThreadLocal;

public class RpcContext {

    private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    public static RpcContext getContext() {
        return LOCAL.get();
    }

    public static void restoreContext(RpcContext Context) {
        LOCAL.set(Context);
    }

    public static void removeContext() {
        LOCAL.remove();
    }

}
