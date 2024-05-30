package com.example.rpc.registry;

import com.example.rpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * @author iumyxF
 * @description: 注册中心服务本地缓存
 * @date 2024/5/14 13:55
 */
public class RegistryServiceCache {

    /**
     * 服务缓存
     */
    public List<ServiceMetaInfo> serviceCache;

    /**
     * 写缓存
     *
     * @param service 服务
     * @return void
     */
    public void writeCache(List<ServiceMetaInfo> service) {
        this.serviceCache = service;
    }

    /**
     * 读缓存
     *
     * @return 缓存服务
     */
    public List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        this.serviceCache = null;
    }
}
