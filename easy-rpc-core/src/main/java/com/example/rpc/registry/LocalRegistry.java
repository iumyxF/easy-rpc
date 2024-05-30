package com.example.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author iumyxF
 * @description: 本地注册中心 测试使用
 * @date 2024/5/14 14:22
 */
public class LocalRegistry {

    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> CLASS_MAP = new ConcurrentHashMap<>();

    /**
     * 注册服务
     *
     * @param serviceName 服务名称
     * @param implClass   服务实例
     */
    public static void register(String serviceName, Class<?> implClass) {
        CLASS_MAP.put(serviceName, implClass);
    }

    /**
     * 获取服务
     *
     * @param serviceName 服务名称
     * @return 服务实例
     */
    public static Class<?> get(String serviceName) {
        return CLASS_MAP.get(serviceName);
    }

    /**
     * 删除服务
     *
     * @param serviceName 服务名称
     */
    public static void remove(String serviceName) {
        CLASS_MAP.remove(serviceName);
    }
}
