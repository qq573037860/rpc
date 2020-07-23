package com.sjq.rpc.domain;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerConfig implements Cloneable {

    private List<URI> serverUrls = new ArrayList<>();

    private String serverIp;

    private String registerCenterUrl;

    private String registerServiceName = Constants.DEFAULT_SERVICE_NAME;

    private int serverPort = Constants.DEFAULT_SERVER_PORT;

    private int connectTimeout = Constants.DEFAULT_CONNECT_TIMEOUT;

    private int requestTimeout = Constants.DEFAULT_REQUEST_TIMEOUT;

    private int heartbeatTimeout = Constants.DEFAULT_HEARTBEAT;

    public ServerConfig() {}

    public ServerConfig(String serverUrl) {
        setServerUrl(serverUrl);
    }

    public ServerConfig(int serverPort) {
        this.serverPort = serverPort;
    }

    public ServerConfig(String serverUrl, int connectTimeout, int requestTimeout, int heartbeatTimeout) {
        this(serverUrl);
        this.connectTimeout = connectTimeout;
        this.requestTimeout = requestTimeout;
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public List<URI> getServerUrls() {
        return serverUrls;
    }

    public void setServerUrl(String serverUrl) {
        if (Objects.nonNull(serverUrl) && serverUrl.length() > 0) {
            String scheme = null;
            for (String url : serverUrl.split(",")) {
                URI uri;
                try {
                    uri = new URI(url);
                } catch (URISyntaxException e) {
                    throw new RpcException(e);
                }
                if (Objects.isNull(scheme)) {
                    scheme = uri.getScheme();
                } else if (!scheme.equals(uri.getScheme())) {
                    throw new RpcException("do not support set multiple protocols at once");
                }
                serverUrls.add(uri);
            }
        }
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getRegisterCenterUrl() {
        return registerCenterUrl;
    }

    public void setRegisterCenterUrl(String registerCenterUrl) {
        this.registerCenterUrl = registerCenterUrl;
    }

    public String getRegisterServiceName() {
        return registerServiceName;
    }

    public void setRegisterServiceName(String registerServiceName) {
        this.registerServiceName = registerServiceName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    public boolean isRegisterCenterForClient() {
        if (Objects.isNull(serverUrls) && serverUrls.isEmpty()) {
            throw new RpcException(RpcException.INVALID_ARGUMENT_EXCEPTION, "serverUrl is null");
        }
        return "register".equals(serverUrls.get(0).getScheme());
    }

    public boolean isRegisterCenterForServer() {
        return Objects.nonNull(registerCenterUrl) && registerCenterUrl.length() > 0;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
