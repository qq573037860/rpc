package com.sjq.rpc;

import com.sjq.rpc.domain.Constants;
import com.sjq.rpc.domain.ServerConfig;
import com.sjq.rpc.domain.register.RegisterInfo;
import com.sjq.rpc.protocol.DefaultProtocol;
import com.sjq.rpc.protocol.Protocol;
import com.sjq.rpc.proxy.JavassistProxyFactory;
import com.sjq.rpc.proxy.RpcServer;
import com.sjq.rpc.support.PackageScanner;
import com.sjq.rpc.support.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RpcServerBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerBootstrap.class);

    private String scanPackage;
    private int port;
    private String ip;
    private RegisterInfo registerInfo;
    private ServerConfig serverConfig = new ServerConfig();
    private Protocol protocol;

    public void start() {
        initDefault();
        registerService();
    }

    private void initDefault() {
        if (Objects.isNull(scanPackage)) {
            scanPackage = "";
        }
        if (port > 0) {
            serverConfig.setServerPort(port);
        }
        if (StringUtils.isNotEmpty(ip)) {
            serverConfig.setServerIp(ip);
        }
        if (Objects.nonNull(registerInfo)) {
            serverConfig.setRegister(registerInfo);
        }
        serverConfig.setHeartbeatTimeout(Constants.DEFAULT_HEARTBEAT*2);
        protocol = new DefaultProtocol(new JavassistProxyFactory());
    }

    private void registerService() {
        PackageScanner.scanClassByPackagePathAndAnnotation(scanPackage, new Class[]{RpcServer.class}).forEach(cls -> {
            try {
                protocol.referToInvoker(cls.newInstance(), cls.getInterfaces()[0], serverConfig);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            logger.info(cls + "服务暴露成功");
        });
    }

    public RpcServerBootstrap scanPage(String scanPage) {
        this.scanPackage = scanPage;
        return this;
    }

    public RpcServerBootstrap port(int port) {
        this.port = port;
        return this;
    }

    public RpcServerBootstrap ip(String ip) {
        this.ip = ip;
        return this;
    }

    public RpcServerBootstrap register(RegisterInfo registerInfo) {
        this.registerInfo = registerInfo;
        return this;
    }
}
