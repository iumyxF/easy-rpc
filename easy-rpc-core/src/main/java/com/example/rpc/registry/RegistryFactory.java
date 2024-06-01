package com.example.rpc.registry;

import com.example.rpc.spi.SpiLoader;

/**
 * @author iumyxF
 * @description: 注册中心 工厂
 * @date 2024/6/1 10:50
 */
public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    public static Registry getNacosInstance() {
        return getInstance(RegistryKeys.NACOS);
    }

    public static Registry getEtcdInstance() {
        return getInstance(RegistryKeys.ETCD);
    }

    public static Registry getZookeeperInstance() {
        return getInstance(RegistryKeys.ZOOKEEPER);
    }

    /**
     * 获取实例
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class, key);
    }
}
