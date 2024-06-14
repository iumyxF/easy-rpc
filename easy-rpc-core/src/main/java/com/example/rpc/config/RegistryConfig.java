package com.example.rpc.config;

import com.example.rpc.registry.RegistryKeys;
import lombok.Data;

/**
 * @author iumyxF
 * @description: 注册中心配置信息
 * @date 2024/5/14 14:32
 */
@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registry = RegistryKeys.ETCD;

    /**
     * 注册中心地址
     * nacos:8848
     * etcd:2379
     */
    private String address = "http://localhost:2379";

    /**
     * 超时时间（单位毫秒）
     */
    private Long timeout = 10000L;
}
