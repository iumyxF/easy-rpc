package com.example.rpc.config;

import com.example.rpc.model.RpcConstant;
import com.example.rpc.serializer.SerializerKeys;
import com.example.rpc.server.NetServerKeys;
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
    private String version = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 服务器主机名
     */
    private String serverHost = "localhost";

    /**
     * 服务器端口号
     */
    private Integer serverPort = 9001;

    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * HTTP服务器类型
     */
    private String httpServer = NetServerKeys.VERTX_TCP;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}
