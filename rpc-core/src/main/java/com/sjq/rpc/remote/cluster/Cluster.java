package com.sjq.rpc.remote.cluster;

public interface Cluster {

    ClusterClientInvoker join(Directory directory);

}
