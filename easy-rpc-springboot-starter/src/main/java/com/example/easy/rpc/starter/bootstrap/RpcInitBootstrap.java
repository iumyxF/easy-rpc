package com.example.easy.rpc.starter.bootstrap;

import com.example.easy.rpc.starter.annotation.EnableRpc;
import com.example.rpc.RpcApplication;
import com.example.rpc.config.RpcConfig;
import com.example.rpc.server.HttpServerFactory;
import com.example.rpc.server.NetServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

/**
 * @author iumyxF
 * @description: Rpc 框架启动
 * @date 2024/6/1 11:37
 */
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(RpcInitBootstrap.class);

    /**
     * 注册Bean前启动HTTP服务器
     *
     * @param importingClassMetadata annotation metadata of the importing class
     * @param registry               current bean definition registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 EnableRpc 注解的属性值
        boolean enableServer = (boolean) Objects.requireNonNull(importingClassMetadata.
                        getAnnotationAttributes(EnableRpc.class.getName()))
                .get("enable");
        RpcApplication.init();
        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 启动服务器
        if (enableServer) {
            NetServer httpServer = HttpServerFactory.getInstance(rpcConfig.getHttpServer());
            httpServer.start(rpcConfig.getServerPort());
        } else {
            log.info("enable is false");
        }
    }
}
