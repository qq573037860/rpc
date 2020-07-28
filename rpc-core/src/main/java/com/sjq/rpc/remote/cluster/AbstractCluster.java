package com.sjq.rpc.remote.cluster;

public abstract class AbstractCluster implements Cluster {

    @Override
    public ClusterClientInvoker join(Directory directory) {
        return doJoin(directory);
    }

    abstract protected ClusterClientInvoker doJoin(Directory directory);
}
