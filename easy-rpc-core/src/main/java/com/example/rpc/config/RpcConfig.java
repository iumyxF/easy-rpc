package com.example.rpc.config;

import com.example.rpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * @author iumyxF
 * @description: RPC 框架全局配置
 * @date 2024/6/1 10:43
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name = "easy-rpc";

    /**
     * 版本号
     */
    private String version = "1.0";

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 8080;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}
