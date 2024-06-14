package com.example.rpc.registry;

import com.example.rpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author iumyxF
 * @description: 注册中心服务本地缓存
 * @date 2024/5/14 13:55
 */
public class RegistryServiceCache {

    /**
     * 服务缓存 key = ServiceMetaInfo.getServiceKey()
     */
    private static final Map<String, List<ServiceMetaInfo>> SERVICE_CACHE = new ConcurrentHashMap<>(16);

    /**
     * 写缓存
     *
     * @param service 服务
     * @return void
     */
    public void writeCache(String serviceKey, List<ServiceMetaInfo> service) {
        if (null == service || service.isEmpty()) {
            return;
        }
        SERVICE_CACHE.put(serviceKey, service);
    }

    /**
     * 读缓存
     *
     * @return 缓存服务
     */
    public List<ServiceMetaInfo> readCache(String serviceKey) {
        return SERVICE_CACHE.get(serviceKey);
    }

    /**
     * 清空缓存
     *
     * @param serviceKey 缓存Key
     */
    public void clearCache(String serviceKey) {
        SERVICE_CACHE.remove(serviceKey);
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        SERVICE_CACHE.clear();
    }
}
