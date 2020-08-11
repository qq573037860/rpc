package com.sjq.rpc.domain.register;

public class RegisterInfo {

    private String url;

    private String type;

    private String serviceName;

    public RegisterInfo(String url, String type) {
        this(url, type, null);
    }

    public RegisterInfo(String url, String type, String serviceName) {
        this.url = url;
        this.type = type;
        this.serviceName = serviceName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public static RegisterInfo convertToDomain(RegisterAnnotation registerAnnotation) {
        return new RegisterInfo(registerAnnotation.url(), registerAnnotation.type(), registerAnnotation.serviceName());
    }
}
