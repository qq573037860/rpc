package com.sjq.rpc.domain;


import com.sjq.rpc.domain.register.RegisterInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerConfig {

    private List<URI> serverUrls = new ArrayList<>();

    private String serverIp;

    private RegisterInfo register;

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
            for (String url : serverUrl.split(",")) {
                URI uri;
                try {
                    uri = new URI(url);
                } catch (URISyntaxException e) {
                    throw new RpcException(e);
                }
                if (!StringUtils.isEmpty(uri.getScheme())) {
                    throw new RpcException(String.format("do not support %s protocol", uri.getScheme()));
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

    public boolean isRegisterCenter() {
        return Objects.nonNull(register);
    }

    public RegisterInfo getRegister() {
        return register;
    }

    public void setRegister(RegisterInfo register) {
        this.register = register;
    }

    public ServerConfig clone() {
        ServerConfig target = new ServerConfig();
        BeanUtils.copyProperties(this, target);
        return target;
    }
}
