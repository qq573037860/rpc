package com.sjq.rpc.domain;

public class Instance {

    private String serviceName;

    private String ip;

    private int port;

    private boolean healthy;

    public Instance(String serviceName) {
        this.serviceName = serviceName;
    }

    public Instance(String serviceName, String ip, int port, boolean healthy) {
        this.serviceName = serviceName;
        this.ip = ip;
        this.port = port;
        this.healthy = healthy;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "serviceName='" + serviceName + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", healthy=" + healthy +
                '}';
    }
}
