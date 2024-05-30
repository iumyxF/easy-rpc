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
    private String registry = RegistryKeys.NACOS;

    /**
     * 注册中心地址
     */
    private String address = "http://localhost:8848";

    /**
     * 超时时间（单位毫秒）
     */
    private Long timeout = 10000L;
}
