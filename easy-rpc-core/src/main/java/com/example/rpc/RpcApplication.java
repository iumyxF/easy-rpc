package com.example.rpc;

import com.example.rpc.config.RegistryConfig;
import com.example.rpc.config.RpcConfig;
import com.example.rpc.registry.Registry;
import com.example.rpc.registry.RegistryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author iumyxF
 * @description:
 * @date 2024/6/1 14:42
 */
public class RpcApplication {

    private static final Logger log = LoggerFactory.getLogger(RpcApplication.class);

    private static volatile RpcConfig rpcConfig;

    /**
     * 如果没有RpcConfig 则使用默认配置
     */
    public static void init() {
        init(new RpcConfig());
    }

    /**
     * 初始化配置中心
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);
        // 创建并注册 Shutdown Hook，JVM 退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 单例模式
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
