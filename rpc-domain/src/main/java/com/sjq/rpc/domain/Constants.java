package com.sjq.rpc.domain;

public interface Constants {

    int DEFAULT_HEARTBEAT = 1000 * 60;

    int DEFAULT_CONNECT_TIMEOUT = 3000;

    int DEFAULT_REQUEST_TIMEOUT = 3000;

    int DEFAULT_SERVER_PORT = 80;

    int CLIENT_RETRY_TIME = 5;

    int CLIENT_RETRY_INTERVAL = 3000;

    String DEFAULT_SERVICE_NAME = "RPC_SERVICE";
}
