package com.sjq.rpc.remote.cluster;

public class DefaultCluster extends AbstractCluster {

    @Override
    protected ClusterClientInvoker doJoin(Directory directory) {
        return new DefaultClusterClientInvoker(directory);
    }

}
